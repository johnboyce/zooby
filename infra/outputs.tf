output "zooby_activations_table_name" {
  value       = module.activations_table_local[0].table_name
  description = "The name of the DynamoDB activations table deployed in the selected environment."
}

output "zooby_models_table_name" {
  value       = module.models_table_local[0].table_name
  description = "The name of the DynamoDB models table deployed in the selected environment."
}

output "zooby_inventory_table_name" {
  value       = module.inventory_table_local[0].table_name
  description = "The name of the DynamoDB inventory table deployed in the selected environment."
}

