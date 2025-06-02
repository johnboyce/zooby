# DynamoDB Module

This module creates an AWS DynamoDB table with configurable attributes and tags.

## Usage
```hcl
module "dynamodb" {
  source      = "./modules/dynamodb"
  table_name  = "my-table"
  environment = "dev"
  hash_key    = "id"
  attributes = [
    {
      name = "id"
      type = "S"
    }
  ]
  tags = {
    Project     = "zooby"
    Environment = "dev"
  }
}
```

## Inputs
| Name        | Description                                 | Type   | Required |
|-------------|---------------------------------------------|--------|----------|
| table_name  | Name of the DynamoDB table                  | string | yes      |
| environment | Deployment environment (dev, prod, local)   | string | yes      |
| hash_key    | Name of the hash (partition) key            | string | yes      |
| attributes  | List of attribute objects (name, type)      | list(object) | yes |
| tags        | Map of tags to apply to the table           | map    | no       |

## Outputs
| Name      | Description                        | Sensitive |
|-----------|------------------------------------|-----------|
| table_name| The name of the DynamoDB table      | no        |

## Best Practices
- All resources are tagged with project and environment.
- Use attribute definitions to match your application's access patterns.

---

See the root README for environment and remote state setup.

