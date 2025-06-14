variable "name" {
  description = "The name of the ECS cluster"
  type        = string
}

variable "vpc_id" {
  description = "The VPC ID used for ECS cluster and related resources"
  type        = string
}

variable "alb_security_group_id" {
  type        = string
  description = "Security group ID of the ALB"
}
