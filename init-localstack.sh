#!/bin/bash
echo "Creating DynamoDB table..."

awslocal dynamodb create-table \
  --table-name zooby_activation \
  --attribute-definitions AttributeName=macAddress,AttributeType=S AttributeName=transactionId,AttributeType=S \
  --key-schema AttributeName=macAddress,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[
    {
      "IndexName": "transactionId-index",
      "KeySchema": [{"AttributeName":"transactionId","KeyType":"HASH"}],
      "Projection": {"ProjectionType":"ALL"}
    }
  ]'

echo "Creating SQS queue..."

awslocal sqs create-queue --queue-name zooby-activation-queue

echo "Done."
