# ======================
# Configuration Variables
# ======================
TERRAFORM_ENV ?= local

# ======================
# Composite Targets
# ======================
all: build ## Default target: build the backend

check: ## Run build, lint, tests, and terraform plan
	$(MAKE) build
	$(MAKE) lint
	$(MAKE) test
	$(MAKE) tf-plan

check-core:
	$(MAKE) build
	$(MAKE) lint
	$(MAKE) test

# ======================
# Docker & Backend Targets
# ======================
up: ## Start local services using Docker Compose
	docker-compose up -d

build: ## Build the backend project
	cd backend && ./mvnw clean package

test: ## Run backend tests
	cd backend && ./mvnw test

dev: ## Run Quarkus in dev mode
	cd backend && ./mvnw quarkus:dev

native: ## Build native image
	cd backend && ./mvnw clean package -Pnative

native-run: ## Run native binary
	cd backend && ./target/zooby-backend-1.0.0-runner

BACKEND_IMAGE_NAME := zooby-backend
BACKEND_ECR_URI := $(ACCOUNT_ID).dkr.ecr.$(AWS_REGION).amazonaws.com/$(BACKEND_IMAGE_NAME)

build-backend-docker: native ## Build Docker image for native Quarkus
	docker build -t $(BACKEND_ECR_URI):latest \
		-t $(BACKEND_ECR_URI):$(GIT_SHA) \
		-f backend/Dockerfile.native backend

push-backend-docker: login-ecr ## Push backend image with latest and short SHA
	docker push $(BACKEND_ECR_URI):latest
	docker push $(BACKEND_ECR_URI):$(GIT_SHA)

deploy-backend-ecr: build-backend-docker push-backend-docker ## Build and push backend
	@echo "✅ Backend image deployed: $(GIT_SHA)"

# ======================
# Frontend Targets
# ======================
.PHONY: frontend
frontend: ## Install deps and build frontend
	cd frontend && npm ci && npm run build

frontend-time:
	echo '{"deployedAt": "'$(date -u '+%Y-%m-%dT%H:%M:%SZ')'"}' > frontend/public/deploy-meta.json
	@echo "Frontend deployment time updated."

frontend-dev: frontend-time ## Start frontend dev server
	cd frontend && npm run dev

deploy-ui: frontend ## Deploy frontend to GitHub Pages
	@echo "Deploying to GitHub Pages..."
	@cd frontend && touch out/.nojekyll
	@cd frontend && git add out/ && git commit -m "Deploy to GitHub Pages" || true
	@cd frontend && git subtree push --prefix out origin gh-pages || \
	(git push origin `git subtree split --prefix out HEAD`:gh-pages --force && \
	echo "Deployed to GitHub Pages")

lint: ## Lint frontend code
	cd frontend && npm ci && npm run lint

# ======================
# Terraform Targets
# ======================
tf-init: ## Initialize Terraform
	cd infra && terraform init -backend-config=backend/$(TERRAFORM_ENV).backend.conf $(if $(RECONFIGURE),-reconfigure)

tf-validate:
	cd infra && terraform validate

tf-plan: ## Plan Terraform changes for selected environment
	cd infra && terraform plan -var-file=environments/$(TERRAFORM_ENV).tfvars

tf-apply: ## Apply Terraform for selected environment
	cd infra && terraform apply -var-file=environments/$(TERRAFORM_ENV).tfvars -auto-approve

tf-destroy: ## Destroy Terraform infra for selected environment
	cd infra && terraform destroy -var-file=environments/$(TERRAFORM_ENV).tfvars -auto-approve

infra-bootstrap: ## Init and apply terraform for selected environment
	cd infra && terraform init -backend-config=backend/$(TERRAFORM_ENV).backend.conf && terraform apply -var-file=environments/$(TERRAFORM_ENV).tfvars -auto-approve

dev-full: infra-local seed-localstack run-backend

infra-local:
	cd infra && terraform apply -var-file=environments/local.tfvars -auto-approve

seed-localstack: ## Seed LocalStack DynamoDB
	@echo "Seeding LocalStack DynamoDB..."
	DYNAMODB_ENDPOINT=http://localhost:4566 python3 seed/seed_dynamodb.py

seed-qa: ## Seed QA DynamoDB
	@echo "Seeding QA DynamoDB..."
	ENV=qa python3 seed/seed_dynamodb.py

seed-prod: ## Seed PROD DynamoDB
	@echo "Seeding Prod DynamoDB..."
	ENV=prod python3 seed/seed_dynamodb.py

run-backend:
	cd backend && ./mvnw clean compile quarkus:dev

tf-workspace: ## Show or select the current Terraform workspace
	cd infra && terraform workspace list && terraform workspace show

tf-output: ## Show Terraform outputs for the selected environment
	cd infra && terraform output

# ----------- CONFIG -----------
AWS_REGION       := us-east-1
ACCOUNT_ID       := 020157571320
IMAGE_NAME       := zooby-frontend
ECR_URI          := $(ACCOUNT_ID).dkr.ecr.$(AWS_REGION).amazonaws.com/$(IMAGE_NAME)
GIT_SHA          := $(shell git rev-parse --short HEAD)
TAG              := $(GIT_SHA)
SERVICE_ARN      := arn:aws:apprunner:$(AWS_REGION):$(ACCOUNT_ID):service/zooby-frontend-qa/3dd179a234f74a128c01728dba6aeda7
BACKEND_IMAGE_NAME := zooby-backend
BACKEND_ECR_URI    := 020157571320.dkr.ecr.us-east-1.amazonaws.com/$(BACKEND_IMAGE_NAME)
# ----------- TARGETS -----------

deploy-qa-ui: push-image trigger-apprunner print-version

push-image: login-ecr
	echo '{ \
  	  "gitSha": "$(GIT_SHA)", \
  	  "deployedAt": "'$$(date -u +"%Y-%m-%dT%H:%M:%SZ")'" \
  	}' > ./frontend/public/deploy-meta.json
	docker build -t $(ECR_URI):$(TAG) -t $(ECR_URI):qa -t $(ECR_URI):latest --build-arg GIT_SHA=$(GIT_SHA) ./frontend
	docker push $(ECR_URI):$(TAG)
	docker push $(ECR_URI):qa
	docker push $(ECR_URI):latest

login-ecr:
	@echo "Logging in to ECR..."
	aws ecr get-login-password --region $(AWS_REGION) | \
	docker login --username AWS --password-stdin $(ECR_URI)

trigger-apprunner:
	@echo "Triggering App Runner deployment with tag $(TAG)..."
	aws apprunner update-service \
		--service-arn $(SERVICE_ARN) \
		--source-configuration ImageRepository="{ImageIdentifier=$(ECR_URI):$(TAG),ImageRepositoryType=ECR,ImageConfiguration={Port=3000}}" \
		--region $(AWS_REGION)

print-version:
	@echo "✅ Deployed version: $(TAG)"

deploy-qa-backend: push-backend-image print-backend-version

push-backend-image: build-backend-docker login-ecr
	docker tag zooby-backend:latest $(BACKEND_ECR_URI):$(TAG)
	docker tag zooby-backend:latest $(BACKEND_ECR_URI):qa
	docker tag zooby-backend:latest $(BACKEND_ECR_URI):latest
	docker push $(BACKEND_ECR_URI):$(TAG)
	docker push $(BACKEND_ECR_URI):qa
	docker push $(BACKEND_ECR_URI):latest

print-backend-version:
	@echo "✅ Backend deployed version: $(TAG)"


# ======================
# JWT Generation
# ======================
# Usage: make jwt ARGS="<userId> <role> <account> [capability1 capability2 ...]"
jwt: ## Generate JWT token using TokenCli
	@cd backend && ./mvnw -q compile exec:java \
		-Dsmallrye.jwt.sign.key.location=src/main/resources/keys/privateKey.pem \
		-Dexec.mainClass="com.zooby.security.TokenCli" \
		-Dexec.args="$(ARGS)" | grep -v "\[INFO\]" | grep -v "\[WARNING\]"

# ======================
# Help Target
# ======================
help: ## Show this help menu
	@echo "Available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-20s %s\n", $$1, $$2}'

.PHONY: all check check-core up build test dev native native-run frontend frontend-time frontend-dev deploy-ui lint tf-init tf-validate tf-plan tf-apply tf-destroy infra-bootstrap dev-full infra-local seed-localstack run-backend jwt help tf-workspace tf-output

