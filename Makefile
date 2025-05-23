all: build

up:
	docker-compose up -d

build:
	cd backend && ./mvnw clean package

frontend:
	cd frontend && npm ci && npm run build

lint:
	cd frontend && npm run lint

test:
	cd backend && ./mvnw test

deploy-ui:
	cd frontend && npm run deploy

terraform:
	cd infra && terraform init && terraform apply -auto-approve
