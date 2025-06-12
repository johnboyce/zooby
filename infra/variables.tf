variable "environment" {
  type        = string
  description = "The deployment environment (e.g., qa, prod, local)"
  validation {
    condition     = contains(["local", "qa", "prod"], var.environment)
    error_message = "Environment must be one of: local, qa, prod."
  }
}

variable "aws_region" {
  type        = string
  description = "AWS region to deploy resources into"
}

variable "zooby_activations_table_name" {
  type        = string
  description = "Name of the DynamoDB activations table"
}

variable "zooby_models_table_name" {
  type        = string
  description = "Name of the DynamoDB models table"
}

variable "zooby_inventory_table_name" {
  type        = string
  description = "Name of the DynamoDB inventory table"
}

variable "zooby_users_table_name" {
  description = "DynamoDB table name for OAuth users"
  type        = string
  default     = "zooby-qa-users"
}


variable "sqs_queue_name" {
  type        = string
  description = "Name of the SQS queue"
}

variable "use_localstack" {
  type        = bool
  description = "Whether to use LocalStack for local development and testing"
}

variable "frontend_service_name" {
  type = string
}

variable "nextauth_url" {
  type        = string
  description = "Public URL of the deployed frontend used for NextAuth callback"
}

variable "oauth_client_secret" {
  description = "OAuth client secret used by NextAuth"
  type        = string
}
