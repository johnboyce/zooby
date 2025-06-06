variable "repo_name" {
  description = "GitHub repo in owner/repo format"
  type        = string
}

variable "role_name" {
  description = "Name of the IAM role for GitHub OIDC"
  type        = string
}
