# ECR Module

This module creates an AWS Elastic Container Registry (ECR) repository with lifecycle policies.

## Features

- Creates ECR repository with mutable image tags
- Configures lifecycle policy to expire untagged images after 7 days
- Applies environment tags for resource management

## Usage

```hcl
module "frontend_ecr" {
  source      = "./modules/ecr"
  repo_name   = "zooby-frontend"
  environment = "qa"
}

module "backend_ecr" {
  source      = "./modules/ecr"
  repo_name   = "zooby-backend"
  environment = "qa"
}
```

## Inputs

| Name        | Description                          | Type   | Required | Default |
|-------------|--------------------------------------|--------|----------|---------|
| repo_name   | Name of the ECR repository           | string | yes      | -       |
| environment | Environment tag (e.g., qa, prod)     | string | yes      | -       |

## Outputs

| Name           | Description                    |
|----------------|--------------------------------|
| repository_url | The URL of the ECR repository  |

## Lifecycle Policy

The module automatically configures a lifecycle policy that:
- Expires untagged images after 7 days
- Helps manage storage costs
- Keeps only tagged images indefinitely

## Docker Commands

After creating the repository, you can push images:

```bash
# Authenticate with ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Build and tag image
docker build -t my-app .
docker tag my-app:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest

# Push to ECR
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest
```

## Best Practices

- Use semantic versioning for image tags (e.g., `v1.0.0`)
- Tag images with git SHA for traceability
- Consider immutable tags for production
- Regularly review and clean up old images

---

See the root README for environment and remote state setup.
