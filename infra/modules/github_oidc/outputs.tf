output "role_arn" {
  description = "The ARN of the IAM role for GitHub OIDC"
  value       = aws_iam_role.this.arn
}

output "oidc_provider_arn" {
  description = "ARN of the GitHub OIDC provider"
  value       = aws_iam_openid_connect_provider.github.arn
}
