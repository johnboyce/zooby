output "dynamodb_table_name" {
  value = length(module.dynamodb_local) > 0 ? module.dynamodb_local[0].table_name : module.dynamodb_aws[0].table_name
}

output "sqs_queue_name" {
  value = length(module.sqs_local) > 0 ? module.sqs_local[0].queue_name : module.sqs_aws[0].queue_name
}
