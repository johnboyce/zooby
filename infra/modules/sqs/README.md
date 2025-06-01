# SQS Module

This module creates an AWS SQS queue.

## Usage
```hcl
module "sqs" {
  source      = "./modules/sqs"
  queue_name  = "my-queue"
  environment = "dev"
  # Optionally add tags
  tags = {
    Project     = "zooby"
    Environment = "dev"
  }
}
```

## Inputs
| Name        | Description                                 | Type   | Required |
|-------------|---------------------------------------------|--------|----------|
| queue_name  | Name of the SQS queue                       | string | yes      |
| environment | Deployment environment (dev, prod, local)   | string | yes      |
| tags        | Map of tags to apply to the queue           | map    | no       |

## Outputs
| Name       | Description                | Sensitive |
|------------|----------------------------|-----------|
| queue_name | The name of the SQS queue  | no        |
| queue_url  | The URL of the SQS queue   | yes       |

## Best Practices
- The `queue_url` output is marked as sensitive.
- All resources are tagged with project and environment.

---

See the root README for environment and remote state setup.
