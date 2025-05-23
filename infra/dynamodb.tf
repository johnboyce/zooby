# Example DynamoDB resource
resource "aws_dynamodb_table" "zooby_activation" {
  provider     = aws.localstack
  name         = "zooby_activation"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "userId"

  attribute {
    name = "userId"
    type = "S"
  }

  attribute {
    name = "transactionId"
    type = "S"
  }

  global_secondary_index {
    name            = "transactionId-index"
    hash_key        = "transactionId"
    projection_type = "ALL"
  }
}