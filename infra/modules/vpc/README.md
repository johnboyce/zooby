# VPC Module

This module creates a VPC with public subnets, internet gateway, and routing configuration for AWS deployments.

## Features

- Creates VPC with DNS support enabled
- Provisions public subnets across multiple availability zones
- Sets up Internet Gateway for outbound connectivity
- Configures route tables for public internet access
- Enables automatic public IP assignment for instances in public subnets

## Usage

```hcl
module "vpc" {
  source               = "./modules/vpc"
  name                 = "zooby-qa"
  vpc_cidr             = "10.0.0.0/16"
  public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
}
```

## Inputs

| Name                | Description                          | Type         | Required | Default        |
|---------------------|--------------------------------------|--------------|----------|----------------|
| name                | VPC name prefix for resource naming  | string       | yes      | -              |
| vpc_cidr            | CIDR block for the VPC               | string       | no       | "10.0.0.0/16"  |
| public_subnet_cidrs | List of CIDR blocks for subnets      | list(string) | yes      | -              |

## Outputs

| Name              | Description                           |
|-------------------|---------------------------------------|
| vpc_id            | The ID of the VPC                     |
| public_subnet_ids | List of public subnet IDs             |

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         VPC (10.0.0.0/16)                       │
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐                     │
│  │  Public Subnet   │  │  Public Subnet   │                     │
│  │   10.0.1.0/24    │  │   10.0.2.0/24    │                     │
│  │    (AZ-1)        │  │    (AZ-2)        │                     │
│  └────────┬─────────┘  └────────┬─────────┘                     │
│           │                     │                               │
│           └──────────┬──────────┘                               │
│                      │                                          │
│              ┌───────┴───────┐                                  │
│              │ Route Table   │                                  │
│              │ 0.0.0.0/0→IGW │                                  │
│              └───────┬───────┘                                  │
│                      │                                          │
│              ┌───────┴───────┐                                  │
│              │    Internet   │                                  │
│              │    Gateway    │                                  │
│              └───────────────┘                                  │
└─────────────────────────────────────────────────────────────────┘
                       │
                       ▼
                   Internet
```

## Resources Created

| Resource                  | Description                                |
|---------------------------|--------------------------------------------|
| `aws_vpc`                 | Main VPC                                   |
| `aws_internet_gateway`    | Internet gateway for public access         |
| `aws_subnet`              | Public subnets (one per CIDR provided)     |
| `aws_route_table`         | Route table for public subnets             |
| `aws_route`               | Default route to Internet Gateway          |
| `aws_route_table_association` | Associates subnets with route table    |

## Availability Zones

The module automatically distributes subnets across available AZs in the region:

```hcl
# Subnet 0 → us-east-1a
# Subnet 1 → us-east-1b
# etc.
```

## CIDR Planning

Recommended CIDR allocation for multiple environments:

| Environment | VPC CIDR       | Subnet 1     | Subnet 2     |
|-------------|----------------|--------------|--------------|
| local       | 10.0.0.0/16    | 10.0.1.0/24  | 10.0.2.0/24  |
| qa          | 10.1.0.0/16    | 10.1.1.0/24  | 10.1.2.0/24  |
| prod        | 10.2.0.0/16    | 10.2.1.0/24  | 10.2.2.0/24  |

## Best Practices

- Use at least 2 subnets in different AZs for high availability
- Plan CIDR blocks to avoid overlaps if VPC peering is needed
- Consider private subnets with NAT Gateway for production workloads
- Use VPC flow logs for network monitoring

## Extending the Module

To add private subnets:

```hcl
# Add to variables.tf
variable "private_subnet_cidrs" {
  type        = list(string)
  description = "List of private subnet CIDR blocks"
  default     = []
}

# Add to main.tf
resource "aws_subnet" "private" {
  count      = length(var.private_subnet_cidrs)
  vpc_id     = aws_vpc.this.id
  cidr_block = var.private_subnet_cidrs[count.index]
  # ... additional configuration
}
```

---

See the root README for environment and remote state setup.
