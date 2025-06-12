variable "name_prefix" {
  description = "Prefix for IAM role names"
  type        = string
}

variable "task_permissions" {
  description = "Custom IAM permissions for ECS task role"
  type = list(object({
    Effect   = string
    Action   = list(string)
    Resource = list(string)
  }))
}
