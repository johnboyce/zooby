variable "service_name" {}
variable "repository_url" {}
variable "branch" {}
variable "connection_name" {}
variable "environment" {}
variable "source_directory" {
  description = "Subdirectory in the GitHub repo where the app source code resides"
  type        = string
  default     = "."  # fallback to repo root if not overridden
}
