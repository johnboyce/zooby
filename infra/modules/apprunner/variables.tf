variable "service_name" {
  description = "The name of the App Runner service"
  type        = string
}

variable "image_identifier" {
  description = "Full ECR image URI with tag (e.g. account.dkr.ecr.region.amazonaws.com/repo:tag)"
  type        = string
}

variable "port" {
  description = "Port the application listens on"
  type        = string
  default     = "3000"
}

variable "cpu" {
  description = "CPU size for the App Runner instance"
  type        = string
  default     = "256"
}

variable "memory" {
  description = "Memory size for the App Runner instance"
  type        = string
  default     = "512"
}

variable "env_vars" {
  description = "Runtime environment variables"
  type        = map(string)
  default     = {}
}

variable "environment" {
  description = "Environment tag (e.g. qa, prod)"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}
