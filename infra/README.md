# Zooby Terraform Infrastructure

## Overview
This directory contains the Terraform code for provisioning and managing Zooby's AWS infrastructure, including DynamoDB tables and SQS queues. The setup is modular, environment-aware, and uses remote state for safe, team-based collaboration.

## Prerequisites
- [Terraform](https://www.terraform.io/downloads.html) >= 1.6.0, < 2.0.0
- AWS CLI configured with credentials that have permissions to manage S3, DynamoDB, and the required AWS resources
- S3 bucket for remote state (e.g., `zooby-terraform-state`)
- DynamoDB table for state locking (e.g., `zooby-terraform-lock`)

## Remote State Setup
Before running Terraform, ensure the following resources exist in your AWS account:
- **S3 Bucket**: `zooby-terraform-state`
- **DynamoDB Table**: `zooby-terraform-lock` (with primary key `LockID` as a string)

You can create them with the AWS CLI:
```sh
aws s3api create-bucket --bucket zooby-terraform-state --region <your-region>
aws dynamodb create-table \
  --table-name zooby-terraform-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region <your-region>
```

## Environments
- `local`, `dev`, and `prod` environments are supported via `infra/environments/*.tfvars` files.
- Select the environment using the `TERRAFORM_ENV` variable (default: `local`).

## Usage
### Initialize
```sh
make tf-init
```

### Plan
```sh
make tf-plan TERRAFORM_ENV=dev
```

### Apply
```sh
make tf-apply TERRAFORM_ENV=dev
```

### Destroy
```sh
make tf-destroy TERRAFORM_ENV=dev
```

### Show Outputs
```sh
make tf-output TERRAFORM_ENV=dev
```

## Module Structure
- `modules/dynamodb`: Reusable DynamoDB table module
- `modules/sqs`: Reusable SQS queue module

## Variable Validation
All variables are validated for type and, where appropriate, allowed values. For example, the `environment` variable only accepts `local`, `dev`, or `prod`.

## Sensitive Outputs
If any outputs contain sensitive data, they are marked as `sensitive = true` in the relevant module's outputs.tf.

## Provider Version Pinning
All modules pin the AWS provider version for reproducibility and safety.

## Terraform Version Constraint
The root module enforces a required Terraform version for consistency across the team.

## Module Documentation
Each module (`modules/dynamodb`, `modules/sqs`) contains its own README.md with usage examples, input/output documentation, and best practices.

## Best Practices
- All resources are tagged with project and environment.
- Remote state is used for safety and collaboration.
- Sensitive outputs should be marked as such in modules if needed.
- Provider and Terraform versions are pinned for reproducibility.

## CI/CD
- GitHub Actions workflows automatically validate, plan, and (optionally) apply changes for all environments.
- Plan files and summaries are uploaded as artifacts for review.

## Troubleshooting
- If you see errors about state locking, check the DynamoDB table and ensure no other runs are in progress.
- For local development, LocalStack is supported via the `use_localstack` variable.

---

For more details, see the Makefile and GitHub Actions workflows in `.github/workflows/`.
