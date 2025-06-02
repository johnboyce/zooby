variable "environment" {
  description = "The environment name (e.g., dev, qa, prod)"
  type        = string
  default     = "dev" # Default value
}
variable "table_name" { type = string }
variable "hash_key" { type = string }
variable "attributes" {
  type = list(object({
    name = string
    type = string
  }))
}
variable "tags" {
  type    = map(string)
  default = {}
}
