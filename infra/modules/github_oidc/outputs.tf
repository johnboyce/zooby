output "role_arn" {
  description = "The ARN of the IAM role for GitHub OIDC"
  value       = aws_iam_role.this.arn
}
