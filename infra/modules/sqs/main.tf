terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

resource "aws_sqs_queue" "this" {
  name = var.queue_name
  tags = {
    Environment = var.environment
  }
}
