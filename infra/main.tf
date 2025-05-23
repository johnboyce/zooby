provider "aws" {
  region                      = "us-east-1"
  access_key                  = "mock_access_key"
  secret_key                  = "mock_secret_key"
  skip_credentials_validation = true
  skip_metadata_api_check     = true

  endpoints {
    dynamodb = "http://localhost:4566"
    sqs      = "http://localhost:4566"
  }
}

resource "aws_dynamodb_table" "zooby_activation" {
  name         = "zooby_activation"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "macAddress"

  attribute {
    name = "macAddress"
    type = "S"
  }
}
