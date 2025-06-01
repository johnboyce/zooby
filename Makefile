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

# ======================
# Frontend Targets
# ======================
frontend: ## Install deps and build frontend
	cd frontend && npm ci && npm run build

frontend-dev: ## Start frontend dev server
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
	cd infra && terraform init

tf-validate: ## Validate Terraform (non-backend, environment-aware)
	cd infra && terraform init -backend=false && terraform validate

tf-plan: ## Plan Terraform changes for selected environment
	cd infra && terraform plan -var-file=environments/$(TERRAFORM_ENV).tfvars

tf-apply: ## Apply Terraform for selected environment
	cd infra && terraform apply -var-file=environments/$(TERRAFORM_ENV).tfvars -auto-approve

tf-destroy: ## Destroy Terraform infra for selected environment
	cd infra && terraform destroy -var-file=environments/$(TERRAFORM_ENV).tfvars -auto-approve

infra-bootstrap: ## Init and apply terraform for selected environment
	cd infra && terraform init && terraform apply cdironments/$(TERRAFORM_ENV).tfvars -auto-approve


dev: infra-local seed-localstack run-backend

infra-local:
	cd infra && terraform apply -var-file=environments/local.tfvars -auto-approve

seed-localstack: ## Seed LocalStack DynamoDB
	@echo "Seeding LocalStack DynamoDB..."
	cd backend && DYNAMODB_ENDPOINT=http://localhost:4566 python3 scripts/seed_dynamodb.py

run-backend:
	cd backend && ./mvnw clean compile quarkus:dev

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
