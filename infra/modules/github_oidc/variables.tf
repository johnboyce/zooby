variable "role_name" {
  description = "Name of the IAM role to create"
  type        = string
}

variable "repo_name" {
  description = "GitHub repo in the form owner/repo"
  type        = string
}

variable "branch" {
  description = "Branch to allow OIDC access from (e.g., main)"
  type        = string
}
