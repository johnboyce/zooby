output "zooby_activations_table_name" {
  value       = length(module.activations_table_local) > 0 ? module.activations_table_local[0].table_name : null
  description = "The name of the DynamoDB activations table deployed in the selected environment."
}

output "zooby_models_table_name" {
  value       = length(module.models_table_local) > 0 ? module.models_table_local[0].table_name : null
  description = "The name of the DynamoDB models table deployed in the selected environment."
}

output "zooby_inventory_table_name_aws" {
  value       = length(module.inventory_table_local) > 0 ? module.inventory_table_local[0].table_name : null
  description = "The name of the DynamoDB inventory table deployed in the selected environment."
}

output "zooby_activations_table_name_aws" {
  value       = length(module.activations_table_aws) > 0 ? module.activations_table_aws[0].table_name : null
  description = "The name of the DynamoDB activations table deployed in the selected environment."
}

output "zooby_models_table_name_aws" {
  value       = length(module.models_table_aws) > 0 ? module.models_table_aws[0].table_name : null
  description = "The name of the DynamoDB models table deployed in the selected environment."
}

output "zooby_inventory_table_name" {
  value       = length(module.inventory_table_aws) > 0 ? module.inventory_table_aws[0].table_name : null
  description = "The name of the DynamoDB inventory table deployed in the selected environment."
}

output "github_actions_role_arn" {
  description = "OIDC role ARN for GitHub Actions"
  value       = module.github_oidc.role_arn
}

output "apprunner_qa_service_url" {
  value       = module.apprunner_qa.service_url
  description = "QA App Runner service URL"
}

output "apprunner_qa_service_arn" {
  value       = module.apprunner_qa.service_arn
  description = "QA App Runner service ARN"
}

output "frontend_ecr_repo_url" {
  value = module.frontend_ecr.repository_url
}

output "backend_ecr_repo_url" {
  value = module.backend_ecr.repository_url
}

output "frontend_ecr_repo_name" {
  value = module.frontend_ecr.repository_name
}

output "backend_ecr_repo_name" {
  value = module.backend_ecr.repository_name
}
