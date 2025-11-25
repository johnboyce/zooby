# GitHub OIDC Module

This module creates IAM resources for GitHub Actions to authenticate with AWS using OpenID Connect (OIDC), eliminating the need for long-lived AWS credentials.

> ⚠️ **Security Warning:** This module currently attaches `AdministratorAccess` policy to the IAM role. This is suitable for development but should be replaced with least-privilege permissions for production environments. See the [Security Considerations](#security-considerations) section for recommended configurations.

## Features

- Creates IAM OIDC identity provider for GitHub Actions
- Creates IAM role assumable by GitHub Actions workflows
- Configures trust policy with repository-level restrictions
- Provides secure, credential-less AWS authentication

## Usage

```hcl
module "github_oidc" {
  source     = "./modules/github_oidc"
  repo_name  = "zooby"
  repo_owner = "johnboyce"
  role_name  = "zooby-github-actions-role"
}
```

## Inputs

| Name       | Description                              | Type   | Required |
|------------|------------------------------------------|--------|----------|
| repo_name  | GitHub repository name                   | string | yes      |
| repo_owner | GitHub repository owner (user or org)    | string | yes      |
| role_name  | Name for the IAM role                    | string | yes      |

## Outputs

| Name     | Description                     |
|----------|---------------------------------|
| role_arn | ARN of the IAM role             |

## How It Works

```
┌────────────────────────────────────────────────────────────────┐
│                   GitHub Actions Workflow                       │
│                                                                 │
│  1. Request OIDC token from GitHub                             │
│     (includes repo info, branch, etc.)                         │
└──────────────────────────┬─────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────────┐
│              AWS IAM OIDC Identity Provider                     │
│                                                                 │
│  2. Validate GitHub's OIDC token                               │
│     - Verify signature against GitHub's public keys            │
│     - Check audience (sts.amazonaws.com)                       │
│     - Verify thumbprint                                        │
└──────────────────────────┬─────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────────┐
│                    IAM Role Trust Policy                        │
│                                                                 │
│  3. Verify trust conditions                                    │
│     - Check repository matches (repo:owner/repo:*)             │
│     - Optional: branch restrictions                            │
│                                                                 │
│  4. Issue temporary AWS credentials via STS                    │
└──────────────────────────┬─────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────────┐
│                    AWS Resources                                │
│                                                                 │
│  5. Workflow can now access AWS resources                      │
│     (ECR, ECS, S3, DynamoDB, etc.)                             │
└────────────────────────────────────────────────────────────────┘
```

## GitHub Actions Workflow Configuration

Configure your workflow to use OIDC:

```yaml
name: Deploy

on:
  push:
    branches: [main]

permissions:
  id-token: write   # Required for OIDC
  contents: read

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: arn:aws:iam::ACCOUNT_ID:role/zooby-github-actions-role
          aws-region: us-east-1

      - name: Deploy to AWS
        run: |
          aws ecs update-service --cluster my-cluster --service my-service
```

## Trust Policy Conditions

The default configuration allows any workflow from the specified repository. You can restrict further:

### Restrict to Specific Branches

```hcl
condition {
  test     = "StringEquals"
  variable = "token.actions.githubusercontent.com:sub"
  values   = [
    "repo:owner/repo:ref:refs/heads/main"
  ]
}
```

### Restrict to Pull Requests

```hcl
values = [
  "repo:owner/repo:pull_request"
]
```

### Restrict to Environment

```hcl
values = [
  "repo:owner/repo:environment:production"
]
```

## Security Considerations

### Current Configuration

> ⚠️ **Note:** This module currently attaches `AdministratorAccess` policy for simplicity. In production, you should:

1. Create a custom policy with minimal required permissions
2. Use separate roles for different workflows
3. Apply the principle of least privilege

### Recommended Production Configuration

```hcl
resource "aws_iam_role_policy" "github_actions" {
  name = "github-actions-policy"
  role = aws_iam_role.this.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = ["ecs:UpdateService"]
        Resource = "arn:aws:ecs:*:*:service/zooby-*"
      }
    ]
  })
}
```

## Benefits Over Long-Lived Credentials

| Aspect           | OIDC                              | Static Credentials           |
|------------------|-----------------------------------|------------------------------|
| Credential Lifetime | Short-lived (1 hour default)   | Long-lived (until rotated)   |
| Rotation         | Automatic                         | Manual                       |
| Secret Storage   | Not needed                        | GitHub Secrets required      |
| Audit Trail      | Token includes workflow context   | Generic API calls            |
| Compromise Risk  | Limited blast radius              | Full access until revoked    |

## Troubleshooting

**"Not authorized to perform sts:AssumeRoleWithWebIdentity"**
- Verify the repository name matches exactly in the trust policy
- Check that `id-token: write` permission is set in the workflow

**"Invalid identity token"**
- Ensure the OIDC provider thumbprint is correct
- GitHub may update their certificates; check for updates

**"The role cannot be assumed"**
- Verify the role ARN is correct
- Check the condition values in the trust policy

---

See the root README for environment and remote state setup.
