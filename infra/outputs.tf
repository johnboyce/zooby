output "zooby_activations_table_name" {
  value = try(module.activations_table_local[0].table_name, module.activations_table_aws[0].table_name)
}

output "zooby_models_table_name" {
  value = try(module.models_table_local[0].table_name, module.models_table_aws[0].table_name)
}

output "zooby_inventory_table_name" {
  value = try(module.inventory_table_local[0].table_name, module.inventory_table_aws[0].table_name)
}

output "zooby_sqs_queue_name" {
  value = try(module.sqs_local[0].queue_name, module.sqs_aws[0].queue_name)
}
