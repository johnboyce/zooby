variable "name" {
  description = "VPC name prefix"
  type        = string
}

variable "vpc_cidr" {
  default     = "10.0.0.0/16"
  description = "CIDR block for the VPC"
}

variable "public_subnet_cidrs" {
  type        = list(string)
  description = "List of public subnet CIDR blocks"
}
