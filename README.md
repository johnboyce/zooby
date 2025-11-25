# 🦓 Zooby

**Zooby** is an event-driven AWS-native application that tracks device activation events using a modern full-stack architecture. Built with enterprise-grade security, observability, and scalability in mind.

## ✨ Key Features

- **GraphQL API** with role-based access control (RBAC)
- **OAuth 2.0/OIDC** authentication integration
- **Real-time device activation tracking**
- **Multi-environment support** (local, QA, production)
- **Infrastructure as Code** with Terraform
- **Native executable builds** via GraalVM for minimal memory footprint

---

## 🚀 Live Demo

🌐 [View the UI on AWS App Runner](https://7qdnizqzpi.us-east-1.awsapprunner.com)

---

## 📦 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (Next.js)                       │
│                     AWS App Runner / GitHub Pages               │
└──────────────────────────────┬──────────────────────────────────┘
                               │ GraphQL/REST
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Backend (Quarkus on ECS Fargate)             │
│         GraphQL API │ OIDC Auth │ OpenTelemetry                 │
└──────────────────────────────┬──────────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              ▼                ▼                ▼
       ┌──────────┐     ┌──────────┐     ┌──────────┐
       │ DynamoDB │     │   SQS    │     │   SNS    │
       │  Tables  │     │  Queues  │     │  Topics  │
       └──────────┘     └──────────┘     └──────────┘
```

---

## 🧰 Tech Stack

| Layer           | Technology                                          |
|-----------------|-----------------------------------------------------|
| Frontend        | Next.js 15, React 19, TailwindCSS 4, NextAuth       |
| Backend         | Quarkus 3.24, SmallRye GraphQL, OIDC, OpenTelemetry |
| Database        | AWS DynamoDB                                        |
| Messaging       | AWS SNS + SQS                                       |
| Compute         | AWS ECS Fargate, App Runner                         |
| Dev Environment | LocalStack, Docker Compose                          |
| Infrastructure  | Terraform 1.6+                                      |
| CI/CD           | GitHub Actions with OIDC authentication             |

---

## 🛠️ Local Development

### Prerequisites

| Requirement     | Version           | Notes                                    |
|-----------------|-------------------|------------------------------------------|
| Java            | 21 (GraalVM)      | Required for native builds               |
| Node.js         | ≥18               | For frontend development                 |
| Docker          | Latest            | For LocalStack and containerized builds  |
| Terraform       | ≥1.6.0, <2.0.0    | Infrastructure management                |
| AWS CLI         | v2                | For AWS service interaction              |
| Make            | Any               | Build automation                         |

### Quick Start

```bash
# Clone the repository
git clone https://github.com/johnboyce/zooby.git
cd zooby

# Start LocalStack for local AWS services
docker-compose up -d

# Initialize and apply local infrastructure
make tf-init TERRAFORM_ENV=local
make tf-apply TERRAFORM_ENV=local

# Seed DynamoDB with test data
make seed-localstack

# Start the backend in dev mode (hot reload enabled)
make dev

# In a separate terminal, start the frontend
cd frontend && npm ci && npm run dev
```

### Development Commands

| Command              | Description                                    |
|----------------------|------------------------------------------------|
| `make dev`           | Start Quarkus backend in dev mode              |
| `make qa`            | Start backend with QA profile                  |
| `make frontend-dev`  | Start Next.js frontend dev server              |
| `make test`          | Run backend tests                              |
| `make lint`          | Run frontend ESLint                            |
| `make build`         | Build backend JAR                              |
| `make check`         | Run build, lint, tests, and Terraform plan     |

### Environment Profiles

The backend supports multiple Quarkus profiles:

| Profile  | Description                     | DynamoDB Tables                |
|----------|---------------------------------|--------------------------------|
| `dev`    | Local development (default)     | `zooby-local-*`                |
| `qa`     | QA/staging environment          | `zooby-qa-*`                   |
| `prod`   | Production environment          | `zooby-prod-*`                 |

Switch profiles:
```bash
make build QUARKUS_PROFILE=qa
make dev QUARKUS_PROFILE=qa
```

---

## 🔐 API Reference

### GraphQL Endpoint

**URL:** `http://localhost:8080/graphql`

**GraphQL UI:** `http://localhost:8080/q/graphql-ui`

### Queries

| Query              | Description                        | Required Role |
|--------------------|------------------------------------|---------------|
| `activationStatus` | Get activation status by txn ID    | `customer`    |
| `eligibility`      | Check device eligibility           | `manager/admin` |
| `zoobyModel`       | Lookup a Zooby model by number     | `user`        |
| `zoobyModels`      | List all models with pagination    | `user`        |
| `inventoryItems`   | List inventory with pagination     | `user`        |

### Mutations

| Mutation    | Description                        | Required Role     |
|-------------|------------------------------------| ------------------|
| `activate`  | Activate a device by MAC address   | `manager/admin`   |

### Example Query

```graphql
query GetModels {
  zoobyModels(filter: "Alpha", offset: 0, limit: 10) {
    model
    name
    description
    imageUrl
  }
}
```

### REST Endpoints

| Endpoint              | Method | Description                    |
|-----------------------|--------|--------------------------------|
| `/q/health`           | GET    | Health check                   |
| `/q/health/live`      | GET    | Liveness probe                 |
| `/q/health/ready`     | GET    | Readiness probe                |
| `/api/image-proxy`    | GET    | Proxy for external images      |

---

## 🧪 Testing

### Backend Tests

```bash
# Run all backend tests
make test

# Run with specific profile
make test QUARKUS_PROFILE=qa
```

### Frontend Linting

```bash
make lint
```

### Full Check (Build + Lint + Test)

```bash
make check-core
```

---

## 🚢 Deployment

### Build Native Image

```bash
# Build native executable (requires GraalVM and Docker)
make native

# Run native binary locally
make native-run
```

### Deploy to AWS

```bash
# Build and push backend Docker image to ECR
make deploy-backend-ecr

# Deploy frontend to App Runner
make deploy-qa-ui

# Apply infrastructure changes
make tf-apply TERRAFORM_ENV=qa
```

### Environment Variables

| Variable               | Description                              | Default                |
|------------------------|------------------------------------------|------------------------|
| `QUARKUS_PROFILE`      | Active Quarkus profile                   | `dev`                  |
| `AWS_REGION`           | AWS region for services                  | `us-east-1`            |
| `NEXTAUTH_URL`         | Frontend callback URL for OAuth          | `http://localhost:3000`|
| `NEXTAUTH_SECRET`      | NextAuth encryption secret               | -                      |
| `OAUTH_CLIENT_SECRET`  | OAuth provider client secret             | -                      |

---

## 📂 Project Structure

```
zooby/
├── backend/                    # Quarkus GraphQL backend
│   ├── src/main/java/com/zooby/
│   │   ├── config/            # Application configuration
│   │   ├── graphql/           # GraphQL resolvers
│   │   ├── model/             # Domain models
│   │   ├── repository/        # Data access layer
│   │   ├── rest/              # REST endpoints
│   │   ├── security/          # Authentication/authorization
│   │   └── service/           # Business logic
│   └── src/main/resources/    # Configuration files
├── frontend/                   # Next.js frontend application
│   ├── app/                   # Next.js App Router pages
│   │   ├── api/               # API routes (NextAuth)
│   │   └── components/        # React components
│   └── public/                # Static assets
├── infra/                      # Terraform infrastructure
│   ├── modules/               # Reusable Terraform modules
│   │   ├── alb/               # Application Load Balancer
│   │   ├── apprunner/         # AWS App Runner
│   │   ├── dynamodb/          # DynamoDB tables
│   │   ├── ecr/               # ECR repositories
│   │   ├── ecs_cluster/       # ECS cluster
│   │   ├── ecs_iam/           # ECS IAM roles
│   │   ├── ecs_service/       # ECS services
│   │   ├── github_oidc/       # GitHub OIDC provider
│   │   ├── sqs/               # SQS queues
│   │   └── vpc/               # VPC networking
│   ├── environments/          # Environment-specific tfvars
│   └── backend/               # Remote state configuration
├── seed/                       # Database seeding scripts
├── .github/workflows/          # GitHub Actions CI/CD
└── Makefile                    # Build automation
```

---

## 🔧 Configuration

### Backend Configuration

Configuration is managed via `application.properties` with profile-specific overrides:

| Property                            | Description                          |
|-------------------------------------|--------------------------------------|
| `zooby.activations.table`           | DynamoDB activations table name      |
| `zooby.models.table`                | DynamoDB models table name           |
| `zooby.inventory.table`             | DynamoDB inventory table name        |
| `zooby.users.table`                 | DynamoDB users table name            |
| `quarkus.oidc.auth-server-url`      | OIDC provider URL                    |
| `quarkus.oidc.client-id`            | OIDC client ID                       |
| `quarkus.http.cors.origins`         | Allowed CORS origins                 |

### Frontend Configuration

Environment variables (set in `.env.local` or App Runner):

| Variable            | Description                    |
|---------------------|--------------------------------|
| `NEXTAUTH_URL`      | Public URL for OAuth callbacks |
| `NEXTAUTH_SECRET`   | Session encryption secret      |
| `OAUTH_CLIENT_ID`   | OAuth provider client ID       |
| `OAUTH_CLIENT_SECRET`| OAuth provider secret         |

---

## 🛡️ Security

### Authentication

- **Backend:** OIDC/JWT validation via Quarkus OIDC extension
- **Frontend:** NextAuth.js with custom OAuth provider

### Authorization

Role-based access control (RBAC) is enforced at the GraphQL resolver level:

| Role       | Capabilities                              |
|------------|-------------------------------------------|
| `user`     | View models and inventory                 |
| `customer` | View activation status                    |
| `manager`  | Check eligibility, activate devices       |
| `admin`    | Full access to all operations             |

### Generating JWT Tokens (for testing)

```bash
# Generate a test JWT token
make jwt ARGS="--user=testuser --roles=user,customer"
```

---

## 📊 Observability

### Logging

- JSON-formatted logs with ELK-friendly field names
- MDC (Mapped Diagnostic Context) includes: `traceId`, `spanId`, `userId`, `roles`
- Log levels configurable per package

### Distributed Tracing

OpenTelemetry integration provides distributed tracing across services.

### Health Checks

```bash
# Liveness check
curl http://localhost:8080/q/health/live

# Readiness check  
curl http://localhost:8080/q/health/ready
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and ensure tests pass: `make check-core`
4. Commit your changes: `git commit -m 'Add my feature'`
5. Push to the branch: `git push origin feature/my-feature`
6. Open a Pull Request

---

## ✅ CI/CD Status

| Workflow     | Status                                                                                           |
|--------------|--------------------------------------------------------------------------------------------------|
| Backend CI   | ![Backend CI](https://github.com/johnboyce/zooby/actions/workflows/backend.yml/badge.svg)        |
| Frontend CI  | ![Frontend CI](https://github.com/johnboyce/zooby/actions/workflows/frontend.yml/badge.svg)      |
| Terraform    | ![Terraform](https://github.com/johnboyce/zooby/actions/workflows/terraform-pr.yml/badge.svg)    |

---

## 🐛 Troubleshooting

### Common Issues

**Backend won't start:**
- Ensure Java 21 is installed: `java -version`
- Check DynamoDB is available (LocalStack running)
- Verify AWS credentials are configured

**Frontend authentication fails:**
- Verify `NEXTAUTH_URL` matches your deployment URL
- Check OAuth provider configuration
- Ensure `NEXTAUTH_SECRET` is set

**Terraform errors:**
- Initialize Terraform: `make tf-init TERRAFORM_ENV=<env>`
- Check AWS credentials
- Verify S3 backend bucket exists

**LocalStack issues:**
- Restart containers: `docker-compose down && docker-compose up -d`
- Check container logs: `docker-compose logs localstack`

---

## 📃 License

MIT © [johnboyce](https://github.com/johnboyce)
