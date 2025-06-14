variable "name" {}
variable "family" {}
variable "cpu" { default = "512" }
variable "memory" { default = "1024" }

variable "cluster_arn" {}
variable "execution_role_arn" {}
variable "task_role_arn" {}

variable "image_url" {}
variable "app_container_name" { default = "zooby-backend" }
variable "container_port" { default = 8080 }

variable "subnet_ids" { type = list(string) }

variable "security_group_ids" {
  type = list(string)
  description = "Security group IDs to associate with the ECS task"
}
variable "desired_count" { default = 1 }

variable "environment_variables" {
  type    = list(object({ name = string, value = string }))
  default = []
}

variable "include_sidecar" { default = false }
variable "sidecar_image" { default = "public.ecr.aws/aws-observability/aws-otel-collector:latest" }
variable "target_group_arn" {
  description = "ALB target group ARN for ECS service"
  type        = string
  default     = null
}

variable "log_group" {
  type        = string
  description = "CloudWatch Logs group for ECS container logs"
  default     = "/ecs/zooby-backend"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "us-east-1"
}
