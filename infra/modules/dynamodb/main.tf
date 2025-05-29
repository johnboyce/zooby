terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

resource "aws_dynamodb_table" "this" {
  name         = var.table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "macAddress"
  attribute {
    name = "macAddress"
    type = "S"
  }
  tags = {
    Environment = var.environment
  }
}
