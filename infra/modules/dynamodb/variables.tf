variable "environment" {
  description = "The environment name (e.g., dev, qa, prod)"
  type        = string
  default     = "dev"
}

variable "table_name" {
  type = string
}

variable "hash_key" {
  type = string
}

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

variable "global_secondary_indexes" {
  description = "List of global secondary indexes"
  type = list(object({
    name            = string
    hash_key        = string
    range_key       = optional(string)
    projection_type = string
  }))
  default = []
}
