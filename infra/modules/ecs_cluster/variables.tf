variable "name" {
  description = "The name of the ECS cluster"
  type        = string
}

variable "vpc_id" {
  description = "The VPC ID used for ECS cluster and related resources"
  type        = string
}
