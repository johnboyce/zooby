output "queue_name" {
  value = aws_sqs_queue.this.name
}

output "queue_url" {
  value       = aws_sqs_queue.this.id
  description = "The URL of the SQS queue."
  sensitive   = true
}

