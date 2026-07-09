# ECS IAM Module

This module creates IAM roles and policies for ECS Fargate tasks.

## Features

- Creates execution role for ECS task management
- Creates task role for application-level permissions
- Attaches standard ECS execution policy
- Supports custom task permissions via configuration

## Usage

```hcl
module "ecs_iam" {
  source       = "./modules/ecs_iam"
  name_prefix  = "zooby-backend"

  task_permissions = [
    {
      Effect   = "Allow"
      Action = [
        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:Query",
        "dynamodb:Scan"
      ]
      Resource = ["arn:aws:dynamodb:us-east-1:*:table/zooby-*"]
    },
    {
      Effect   = "Allow"
      Action   = ["sqs:SendMessage"]
      Resource = ["arn:aws:sqs:us-east-1:*:zooby-*"]
    }
  ]
}
```

## Inputs

| Name             | Description                              | Type         | Required |
|------------------|------------------------------------------|--------------|----------|
| name_prefix      | Prefix for IAM role names                | string       | yes      |
| task_permissions | List of IAM policy statements for tasks  | list(object) | yes      |

## Outputs

| Name               | Description                          |
|--------------------|--------------------------------------|
| execution_role_arn | ARN of the task execution role       |
| task_role_arn      | ARN of the task role                 |

## IAM Roles

### Execution Role

Used by ECS to manage the task lifecycle:
- Pull container images from ECR
- Push logs to CloudWatch
- Retrieve secrets from Secrets Manager (if configured)

Attached policy: `AmazonECSTaskExecutionRolePolicy`

### Task Role

Used by the application running in the container:
- Access DynamoDB tables
- Send messages to SQS queues
- Any custom permissions your application needs

## Task Permissions Structure

Each permission object should have:

```hcl
{
  Effect   = "Allow" | "Deny"
  Action   = ["service:Action", ...]
  Resource = ["arn:aws:...", ...]
}
```

## Example: DynamoDB Permissions

```hcl
task_permissions = [
  {
    Effect   = "Allow"
    Action = [
      "dynamodb:GetItem",
      "dynamodb:PutItem",
      "dynamodb:UpdateItem",
      "dynamodb:DeleteItem",
      "dynamodb:Query",
      "dynamodb:Scan",
      "dynamodb:BatchGetItem",
      "dynamodb:BatchWriteItem",
      "dynamodb:DescribeTable"
    ]
    Resource = ["arn:aws:dynamodb:us-east-1:ACCOUNT_ID:table/zooby-*"]
  },
  {
    Effect = "Allow"
    Action = ["dynamodb:ListTables"]
    Resource = ["*"]
  }
]
```

## Best Practices

- Follow the principle of least privilege
- Use specific resource ARNs instead of wildcards
- Separate read and write permissions when possible
- Use condition keys for additional security
- Regularly audit IAM policies

## Related Modules

- `ecs_cluster`: Creates the ECS cluster
- `ecs_service`: Uses these roles for task definitions

---

See the root README for environment and remote state setup.
