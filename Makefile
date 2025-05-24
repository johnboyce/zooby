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

dev:
	cd backend && ./mvnw quarkus:dev

native:
	cd backend && ./mvnw clean package -Pnative

native-run:
	cd backend && ./target/zooby-backend-1.0.0-runner

deploy-ui:
	cd frontend && npm run deploy

terraform:
	cd infra && terraform init && terraform apply -auto-approve

# =======================
# JWT Generation Commands
# =======================
# Usage:
# make jwt ARGS="<userId> <role> <account> [capability1 capability2 ...]"
#
# Where:
#   <userId>     = unique user identifier (e.g. admin-001)
#   <role>       = one of: customer, manager, admin
#   <account>    = account or tenant ID (e.g. acme-inc)
#   capabilities = optional space-separated list (e.g. restart view-status)
#
# Examples:
# make jwt ARGS="admin-001 admin acme-inc restart delete"
# make jwt ARGS="manager-002 manager sales-dept view-status"
# make jwt ARGS="customer-123 customer cust-42"
#
# The resulting JWT includes:
#   "sub"         = userId
#   "groups"      = [role]
#   "account"     = account ID
#   "capabilities" = array of capabilities
#   "exp"         = 1 hour expiration
#   "iat"         = issued-at timestamp

jwt:
	@cd backend && ./mvnw -q compile exec:java \
		-Dsmallrye.jwt.sign.key.location=src/main/resources/keys/privateKey.pem \
		-Dexec.mainClass="com.zooby.security.TokenCli" \
		-Dexec.args="$(ARGS)" | grep -v "\[INFO\]" | grep -v "\[WARNING\]"
