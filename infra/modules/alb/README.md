# ALB Module

This module creates an Application Load Balancer (ALB) with target groups and listeners for routing traffic to ECS services.

## Features

- Creates internet-facing Application Load Balancer
- Configures HTTP listener with forwarding rules
- Creates target group for backend services with health checks
- Sets up security groups for inbound HTTP traffic

## Usage

```hcl
module "alb" {
  source = "./modules/alb"

  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = module.vpc.public_subnet_ids
}
```

## Inputs

| Name              | Description                          | Type         | Required |
|-------------------|--------------------------------------|--------------|----------|
| vpc_id            | VPC ID for the ALB                   | string       | yes      |
| public_subnet_ids | List of public subnet IDs            | list(string) | yes      |

## Outputs

| Name                          | Description                              |
|-------------------------------|------------------------------------------|
| alb_arn                       | ARN of the Application Load Balancer     |
| alb_dns_name                  | DNS name of the ALB                      |
| zooby_backend_target_group_arn| ARN of the backend target group          |
| security_group_id             | Security group ID for the ALB            |

## Architecture

```
                        Internet
                           │
                           ▼
              ┌────────────────────────┐
              │    Security Group      │
              │  Ingress: 80 (HTTP)    │
              │  Egress: All           │
              └───────────┬────────────┘
                          │
              ┌───────────▼────────────┐
              │  Application Load      │
              │     Balancer           │
              │   (Internet-facing)    │
              └───────────┬────────────┘
                          │
              ┌───────────▼────────────┐
              │    HTTP Listener       │
              │     Port: 80           │
              └───────────┬────────────┘
                          │
              ┌───────────▼────────────┐
              │    Target Group        │
              │    Port: 8080          │
              │    Health: /q/health   │
              └───────────┬────────────┘
                          │
              ┌───────────▼────────────┐
              │   ECS Fargate Tasks    │
              │   (Backend Services)   │
              └────────────────────────┘
```

## Health Check Configuration

The target group is configured with:

| Setting             | Value       | Description                     |
|---------------------|-------------|---------------------------------|
| Path                | `/q/health` | Quarkus health endpoint         |
| Matcher             | 200         | Expected HTTP status code       |
| Interval            | 30 seconds  | Time between health checks      |
| Timeout             | 5 seconds   | Health check timeout            |
| Healthy threshold   | 2           | Consecutive successes needed    |
| Unhealthy threshold | 3           | Consecutive failures needed     |

## Security Group Rules

| Direction | Port | Protocol | Source/Destination | Description          |
|-----------|------|----------|-------------------|----------------------|
| Ingress   | 80   | TCP      | 0.0.0.0/0         | Allow HTTP traffic   |
| Egress    | All  | All      | 0.0.0.0/0         | Allow all outbound   |

## Adding HTTPS Support

To add HTTPS support:

1. Create or import an ACM certificate
2. Add HTTPS listener:

```hcl
resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.zooby.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = var.certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.zooby_backend.arn
  }
}
```

3. Update security group to allow port 443

## Best Practices

- Enable deletion protection in production
- Use HTTPS in production environments
- Configure access logs for debugging
- Set up CloudWatch alarms for target health
- Consider WAF integration for additional security

## Related Modules

- `vpc`: Provides VPC and subnet configuration
- `ecs_service`: Services that register with target groups
- `ecs_cluster`: Security group integration for Fargate tasks

---

See the root README for environment and remote state setup.
