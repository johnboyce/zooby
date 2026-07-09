# Zooby Backend

A Quarkus-based GraphQL API backend for the Zooby device activation tracking system.

## Overview

The backend provides a GraphQL API for:
- Device activation tracking
- Model and inventory management
- User authentication and authorization via OIDC

## Tech Stack

- **Framework:** Quarkus 3.24
- **API:** SmallRye GraphQL
- **Database:** AWS DynamoDB
- **Authentication:** OIDC/JWT
- **Observability:** OpenTelemetry, JSON logging
- **Build:** Maven, GraalVM (native)

## Prerequisites

- Java 21 (GraalVM recommended for native builds)
- Maven 3.8+ (wrapper included)
- Docker (optional, for containerized builds)
- AWS credentials (for DynamoDB access)

## Project Structure

```
backend/
├── src/main/java/com/zooby/
│   ├── config/                 # Application configuration
│   │   ├── HeartbeatLogger.java    # Periodic health logging
│   │   └── StartupLogger.java      # Startup configuration logging
│   ├── graphql/                # GraphQL resolvers
│   │   ├── ActivationResolver.java # Activation queries/mutations
│   │   ├── InventoryResource.java  # Inventory queries
│   │   ├── ModelResource.java      # Model queries
│   │   └── HealthCheckResource.java
│   ├── model/                  # Domain models
│   │   ├── ActivationResponse.java
│   │   ├── ActivationStatus.java
│   │   ├── Eligibility.java
│   │   ├── InventoryItem.java
│   │   └── ZoobyModel.java
│   ├── repository/             # Data access layer
│   │   ├── DynamoDBService.java
│   │   ├── InventoryRepository.java
│   │   ├── UserRepository.java
│   │   └── ZoobyModelRepository.java
│   ├── rest/                   # REST endpoints
│   │   ├── Healthcheck.java
│   │   └── ImageProxyResource.java
│   ├── security/               # Security components
│   │   ├── IdentityAugmentor.java  # JWT claim augmentation
│   │   ├── TokenCli.java           # JWT token generator CLI
│   │   ├── TokenGenerator.java     # Token generation utility
│   │   └── UserContext.java        # Request-scoped user context
│   └── service/                # Business logic
│       ├── ActivationService.java
│       ├── InventoryService.java
│       ├── UserService.java
│       └── ZoobyModelService.java
└── src/main/resources/
    ├── application.properties  # Configuration with profile overrides
    ├── application-test.properties
    ├── schema/                 # JSON schema definitions
    └── keys/                   # JWT signing keys (dev only)
```

## Configuration

### Profiles

| Profile | Purpose | DynamoDB Tables |
|---------|---------|-----------------|
| `dev` (default) | Local development with DevServices | `zooby-local-*` |
| `qa` | QA/staging environment | `zooby-qa-*` |
| `prod` | Production environment | `zooby-prod-*` |

### Key Properties

```properties
# DynamoDB Tables
zooby.activations.table=zooby-local-activations
zooby.models.table=zooby-local-models
zooby.inventory.table=zooby-local-inventory
zooby.users.table=zooby-local-users

# OIDC Configuration
quarkus.oidc.auth-server-url=https://auth.connellboyce.com
quarkus.oidc.client-id=zooby
quarkus.oidc.application-type=service

# CORS (add your origins)
quarkus.http.cors.origins=http://localhost:3000,http://localhost:5173

# Logging
quarkus.log.console.json=true
quarkus.log.level=INFO
quarkus.log.category."com.zooby".level=DEBUG
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `QUARKUS_PROFILE` | Active profile | `dev` |
| `AWS_REGION` | AWS region | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | AWS access key | - |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key | - |

## Running Locally

### Development Mode (with hot reload)

```bash
# From repository root
make dev

# Or directly
cd backend && ./mvnw quarkus:dev
```

Access points:
- GraphQL UI: http://localhost:8080/q/graphql-ui
- Health: http://localhost:8080/q/health
- Dev UI: http://localhost:8080/q/dev-ui

### With QA Profile

```bash
make qa

# Or
cd backend && ./mvnw quarkus:dev -Dquarkus.profile=qa
```

## Building

### JVM Build

```bash
cd backend && ./mvnw clean package -Dquarkus.profile=qa
```

Output: `target/quarkus-app/`

### Native Build

Requires GraalVM and Docker:

```bash
make native

# Or
cd backend && ./mvnw clean package -Pnative \
    -Dquarkus.native.container-build=true \
    -Dquarkus.native.target=linux-x86_64 \
    -Dquarkus.profile=qa
```

Output: `target/zooby-backend-1.0.0-runner`

### Docker Image

```bash
make build-backend-docker
```

## API Reference

### GraphQL Schema

The GraphQL API is available at `/graphql` with the UI at `/q/graphql-ui`.

#### Queries

```graphql
# Get activation status by transaction ID
query ActivationStatus($txnId: String!) {
  activationStatus(transactionId: $txnId) {
    macAddress
    transactionId
    userId
    status
    stepsLog
    updatedAt
  }
}

# Check device eligibility (manager/admin only)
query CheckEligibility($mac: String!) {
  eligibility(macAddress: $mac) {
    macAddress
    eligible
    make
    model
  }
}

# Get a specific model
query GetModel($model: String!) {
  zoobyModel(model: $model) {
    model
    name
    description
    imageUrl
  }
}

# List all models with pagination
query ListModels($filter: String, $offset: Int, $limit: Int) {
  zoobyModels(filter: $filter, offset: $offset, limit: $limit) {
    model
    name
    description
    imageUrl
  }
}

# List inventory items
query ListInventory($filter: String, $offset: Int, $limit: Int) {
  inventoryItems(filter: $filter, offset: $offset, limit: $limit) {
    serialNumber
    model
    status
    location
  }
}
```

#### Mutations

```graphql
# Activate a device (manager/admin only)
mutation ActivateDevice($mac: String!, $make: String!, $model: String!) {
  activate(macAddress: $mac, make: $make, model: $model) {
    transactionId
    success
  }
}
```

### REST Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/q/health` | GET | Combined health check |
| `/q/health/live` | GET | Liveness probe |
| `/q/health/ready` | GET | Readiness probe |
| `/api/image-proxy` | GET | Proxy external images |

## Security

### Authentication

The API uses OIDC for authentication. Tokens are validated against the configured OIDC provider.

### Authorization

Role-based access control is enforced using `@RolesAllowed` annotations:

| Role | Access Level |
|------|--------------|
| `user` | View models, inventory |
| `customer` | View activation status |
| `manager` | Check eligibility, activate devices |
| `admin` | Full access |

### Generating Test Tokens

```bash
# From repository root
make jwt ARGS="--user=testuser --roles=user,customer"
```

## Testing

### Run Tests

```bash
cd backend && ./mvnw test

# Or from root
make test
```

### Test with Specific Profile

```bash
cd backend && ./mvnw test -Dquarkus.profile=qa
```

## Logging

The application uses JSON-formatted logs compatible with ELK Stack:

```json
{
  "@timestamp": "2024-01-15T10:30:00.000Z",
  "log.level": "INFO",
  "message": "Activation successful",
  "logger": "com.zooby.graphql.ActivationResolver",
  "metadata": {
    "traceId": "abc123",
    "spanId": "def456",
    "userId": "user123",
    "roles": "[user, customer]"
  }
}
```

### Log Categories

| Category | Level | Description |
|----------|-------|-------------|
| `com.zooby` | DEBUG | Application code |
| `software.amazon.awssdk` | INFO | AWS SDK |
| `software.amazon.awssdk.auth` | DEBUG | AWS authentication |

## DynamoDB Tables

| Table | Hash Key | Description |
|-------|----------|-------------|
| `zooby-{env}-activations` | `macAddress` | Device activations |
| `zooby-{env}-models` | `model` | Device models |
| `zooby-{env}-inventory` | `serial_number` | Inventory items |
| `zooby-{env}-users` | `user_id` | OAuth users |

## Troubleshooting

### Common Issues

**DynamoDB connection errors:**
- Verify AWS credentials are configured
- Check DynamoDB endpoint URL
- For local dev, ensure LocalStack is running

**Authentication failures:**
- Verify OIDC provider is accessible
- Check token expiration
- Validate JWT claims match expected values

**Native build failures:**
- Ensure GraalVM is installed
- Check Docker is running
- Verify native-image tool is available

### Debug Logging

Enable debug logging for specific packages:

```properties
quarkus.log.category."com.zooby".level=DEBUG
quarkus.log.category."io.quarkus.oidc".level=DEBUG
```

## Dependencies

Key dependencies managed in `pom.xml`:

| Dependency | Version | Purpose |
|------------|---------|---------|
| Quarkus Platform | 3.24.4 | Framework BOM |
| AWS SDK | 2.31.44 | DynamoDB client |
| SmallRye GraphQL | (from Quarkus) | GraphQL implementation |
| Jackson | 2.17.1 | JSON processing |
| Rest Assured | 5.4.0 | Testing |
| AssertJ | 3.26.0 | Test assertions |
