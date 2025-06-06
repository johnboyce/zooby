output "service_url" {
  description = "Public URL of the App Runner service"
  value       = aws_apprunner_service.frontend.service_url
}

output "service_arn" {
  description = "ARN of the App Runner service"
  value       = aws_apprunner_service.frontend.arn
}

output "access_role_arn" {
  value       = aws_iam_role.apprunner_ecr_access.arn
  description = "IAM role used by App Runner to access ECR"
}
