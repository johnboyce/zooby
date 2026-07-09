# ECS Service Module

This module creates an ECS Fargate service with task definition, CloudWatch logging, and optional sidecar containers.

## Features

- Creates ECS task definition for Fargate
- Deploys ECS service with load balancer integration
- Configures CloudWatch Logs with automatic retention
- Supports OpenTelemetry sidecar for observability
- Uses Fargate Spot for cost optimization

## Usage

```hcl
module "zooby_backend" {
  source = "./modules/ecs_service"

  name               = "zooby-backend"
  family             = "zooby-backend-task"
  cluster_arn        = module.ecs_cluster.arn
  
  cpu                = "512"
  memory             = "1024"
  
  execution_role_arn = module.ecs_iam.execution_role_arn
  task_role_arn      = module.ecs_iam.task_role_arn
  target_group_arn   = module.alb.backend_target_group_arn

  image_url          = "account.dkr.ecr.region.amazonaws.com/zooby-backend:qa"
  app_container_name = "zooby-backend"
  container_port     = 8080

  subnet_ids         = module.vpc.public_subnet_ids
  security_group_ids = [module.ecs_cluster.fargate_sg_id]
  aws_region         = "us-east-1"

  desired_count      = 1
  include_sidecar    = true

  environment_variables = [
    { name = "QUARKUS_PROFILE", value = "qa" },
    { name = "AWS_REGION", value = "us-east-1" }
  ]
}
```

## Inputs

| Name                  | Description                                    | Type         | Required | Default |
|-----------------------|------------------------------------------------|--------------|----------|---------|
| name                  | Name of the ECS service                        | string       | yes      | -       |
| family                | Task definition family name                    | string       | yes      | -       |
| cluster_arn           | ARN of the ECS cluster                         | string       | yes      | -       |
| cpu                   | CPU units for the task                         | string       | no       | "512"   |
| memory                | Memory (MB) for the task                       | string       | no       | "1024"  |
| execution_role_arn    | ARN of the execution IAM role                  | string       | yes      | -       |
| task_role_arn         | ARN of the task IAM role                       | string       | yes      | -       |
| target_group_arn      | ALB target group ARN                           | string       | yes      | -       |
| image_url             | Container image URL                            | string       | yes      | -       |
| app_container_name    | Name of the main container                     | string       | yes      | -       |
| container_port        | Port exposed by the container                  | number       | yes      | -       |
| subnet_ids            | List of subnet IDs for task placement          | list(string) | yes      | -       |
| security_group_ids    | List of security group IDs                     | list(string) | yes      | -       |
| aws_region            | AWS region                                     | string       | yes      | -       |
| desired_count         | Number of task instances                       | number       | no       | 1       |
| include_sidecar       | Include OpenTelemetry sidecar                  | bool         | no       | false   |
| environment_variables | List of environment variable objects           | list(object) | no       | []      |
| log_group             | CloudWatch log group name                      | string       | no       | -       |
| sidecar_image         | OpenTelemetry sidecar image                    | string       | no       | -       |

## Outputs

| Name           | Description                          |
|----------------|--------------------------------------|
| service_name   | Name of the ECS service              |
| task_arn       | ARN of the task definition           |

## Resource Allocation

| CPU (vCPU) | Memory Options (MB)          |
|------------|------------------------------|
| 256 (0.25) | 512, 1024, 2048              |
| 512 (0.5)  | 1024, 2048, 3072, 4096       |
| 1024 (1)   | 2048, 3072, 4096, 5120, 6144, 7168, 8192 |
| 2048 (2)   | 4096 - 16384 (in 1024 increments) |
| 4096 (4)   | 8192 - 30720 (in 1024 increments) |

## Architecture

```
              ┌────────────────────────────────────────────────┐
              │                   ECS Service                   │
              │                                                 │
              │  ┌──────────────────────────────────────────┐  │
              │  │            Task Definition                │  │
              │  │                                           │  │
              │  │  ┌─────────────────┐ ┌─────────────────┐ │  │
              │  │  │    App          │ │   OTEL Sidecar  │ │  │
              │  │  │   Container     │ │   (optional)    │ │  │
              │  │  │   Port: 8080    │ │                 │ │  │
              │  │  └─────────────────┘ └─────────────────┘ │  │
              │  │                                           │  │
              │  └──────────────────────────────────────────┘  │
              │                                                 │
              │      ┌──────────────────────────────────────┐  │
              │      │  CloudWatch Logs (7-day retention)   │  │
              │      └──────────────────────────────────────┘  │
              └────────────────────────────────────────────────┘
```

## CloudWatch Logs

The module creates a CloudWatch log group with:
- 7-day retention period
- Log stream prefix matching container name
- Automatic log configuration in task definition

Log group naming: `/ecs/{service-name}`

## Fargate Spot

The service uses Fargate Spot capacity provider for cost optimization:
- Up to 70% cost savings compared to on-demand
- Suitable for fault-tolerant workloads
- May experience interruptions during capacity constraints

## Best Practices

- Use health checks in your application
- Configure proper resource limits
- Enable Container Insights for monitoring
- Use secrets for sensitive environment variables
- Set appropriate desired count for high availability

## Related Modules

- `ecs_cluster`: Creates the ECS cluster
- `ecs_iam`: Creates IAM roles
- `alb`: Creates the load balancer and target groups

---

See the root README for environment and remote state setup.
