# ECS Cluster Module

This module creates an AWS ECS cluster with Fargate capacity providers and associated security groups.

## Features

- Creates ECS cluster with Fargate and Fargate Spot capacity providers
- Configures Fargate Spot as default for cost optimization
- Creates security group for Fargate tasks
- Sets up ingress rules for ALB to reach tasks

## Usage

```hcl
module "ecs_cluster" {
  source                = "./modules/ecs_cluster"
  name                  = "zooby-qa-cluster"
  vpc_id                = module.vpc.vpc_id
  alb_security_group_id = module.alb.security_group_id
}
```

## Inputs

| Name                  | Description                              | Type   | Required |
|-----------------------|------------------------------------------|--------|----------|
| name                  | Name of the ECS cluster                  | string | yes      |
| vpc_id                | VPC ID for security group placement      | string | yes      |
| alb_security_group_id | Security group ID of the ALB             | string | yes      |

## Outputs

| Name          | Description                          |
|---------------|--------------------------------------|
| arn           | The ARN of the ECS cluster           |
| name          | The name of the ECS cluster          |
| fargate_sg_id | Security group ID for Fargate tasks  |

## Capacity Providers

The cluster is configured with two capacity providers:

| Provider      | Use Case                                       |
|---------------|------------------------------------------------|
| FARGATE       | On-demand tasks with guaranteed availability   |
| FARGATE_SPOT  | Cost-optimized tasks (up to 70% cheaper)       |

Default strategy uses FARGATE_SPOT for cost efficiency.

## Security Group Rules

The Fargate security group includes:

| Direction | Port  | Source                | Description                    |
|-----------|-------|----------------------|--------------------------------|
| Ingress   | 8080  | ALB Security Group   | Allow ALB health checks & traffic |
| Egress    | All   | 0.0.0.0/0            | Allow all outbound traffic     |

## Architecture

```
                    Internet
                        │
                        ▼
              ┌─────────────────┐
              │      ALB        │
              │  Security Group │
              └────────┬────────┘
                       │ Port 8080
                       ▼
              ┌─────────────────┐
              │  ECS Cluster    │
              │ ┌─────────────┐ │
              │ │   Fargate   │ │
              │ │   Tasks     │ │
              │ │  (SG: 8080) │ │
              │ └─────────────┘ │
              └─────────────────┘
```

## Best Practices

- Use FARGATE_SPOT for non-critical workloads
- Configure service deployment with minimum healthy percent for Spot
- Set up CloudWatch Container Insights for monitoring
- Use task placement strategies for multi-AZ distribution

## Related Modules

- `ecs_service`: Creates ECS services that run on this cluster
- `ecs_iam`: Creates IAM roles for task execution
- `alb`: Creates Application Load Balancer

---

See the root README for environment and remote state setup.
