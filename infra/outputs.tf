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

# output "apprunner_service_url" {
#   description = "Public URL of the App Runner service"
#   value       = module.apprunner_qa.frontend_url
# }
