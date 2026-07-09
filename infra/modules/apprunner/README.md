# App Runner Module

This module creates an AWS App Runner service for deploying containerized applications from ECR.

## Features

- Deploys container images from ECR
- Configures auto-scaling with customizable settings
- Supports runtime environment variables
- Creates IAM role for ECR access
- Configurable CPU and memory allocation

## Usage

```hcl
module "apprunner_qa" {
  source = "./modules/apprunner"

  service_name     = "zooby-frontend-qa"
  image_identifier = "020157571320.dkr.ecr.us-east-1.amazonaws.com/zooby-frontend:latest"
  environment      = "qa"
  aws_region       = "us-east-1"

  port   = "3000"
  cpu    = "256"   # 0.25 vCPU
  memory = "512"   # 0.5 GB

  env_vars = {
    NODE_ENV        = "production"
    NEXTAUTH_URL    = "https://example.awsapprunner.com"
    NEXTAUTH_SECRET = "your-secret"
  }
}
```

## Inputs

| Name             | Description                                      | Type        | Required | Default |
|------------------|--------------------------------------------------|-------------|----------|---------|
| service_name     | Name of the App Runner service                   | string      | yes      | -       |
| image_identifier | Full ECR image URI with tag                      | string      | yes      | -       |
| port             | Port the application listens on                  | string      | no       | "3000"  |
| cpu              | CPU allocation (256, 512, 1024, 2048, 4096)      | string      | no       | "256"   |
| memory           | Memory allocation in MB (512, 1024, 2048, etc.)  | string      | no       | "512"   |
| env_vars         | Runtime environment variables                    | map(string) | no       | {}      |
| environment      | Environment tag (e.g., qa, prod)                 | string      | yes      | -       |
| aws_region       | AWS region                                       | string      | yes      | -       |

## Outputs

| Name        | Description                        |
|-------------|------------------------------------|
| service_url | The URL of the App Runner service  |
| service_arn | The ARN of the App Runner service  |

## Auto-Scaling Configuration

The module configures auto-scaling with the following defaults:
- Minimum instances: 1
- Maximum instances: 2
- Maximum concurrency: 50 requests per instance

## IAM Role

The module creates an IAM role with `AmazonEC2ContainerRegistryReadOnly` policy attached for pulling images from ECR.

## Resource Allocation

| CPU (vCPU) | Memory Options (MB)          |
|------------|------------------------------|
| 256 (0.25) | 512, 1024                    |
| 512 (0.5)  | 1024, 2048                   |
| 1024 (1)   | 2048, 3072, 4096             |
| 2048 (2)   | 4096, 6144, 8192, 10240      |
| 4096 (4)   | 8192, 12288, 14336, 16384    |

## Deployment

Auto-deployments are disabled by default. To deploy a new image version:

```bash
aws apprunner update-service \
  --service-arn <service_arn> \
  --source-configuration ImageRepository="{ImageIdentifier=<new_image>,ImageRepositoryType=ECR,ImageConfiguration={Port=3000}}" \
  --region us-east-1
```

Or use the Makefile command:

```bash
make trigger-apprunner
```

## Best Practices

- Use specific image tags (not `latest`) in production
- Configure health checks for reliable deployments
- Set appropriate auto-scaling based on expected traffic
- Use AWS Secrets Manager for sensitive environment variables
- Monitor costs as App Runner charges per vCPU-hour

## Troubleshooting

**Service fails to start:**
- Check image exists in ECR
- Verify IAM role permissions
- Review CloudWatch logs for application errors

**Connection refused:**
- Verify application listens on configured port
- Check health check endpoint responds

---

See the root README for environment and remote state setup.
