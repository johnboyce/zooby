import os
import json
import boto3
from pathlib import Path

import os
import boto3

def get_dynamodb_client():
  endpoint_url = os.environ.get("DYNAMODB_ENDPOINT")
  if endpoint_url:
    return boto3.client(
      "dynamodb",
      region_name=os.environ.get("AWS_REGION", "us-east-1"),
      aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID", "test"),
      aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY", "test"),
      endpoint_url=endpoint_url,
    )
  else:
    return boto3.client(
      "dynamodb",
      region_name=os.environ.get("AWS_REGION", "us-east-1"),
      aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID", "test"),
      aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY", "test"),
    )

def convert_to_dynamo_type(value):
  if isinstance(value, bool):
    return {"BOOL": value}
  elif isinstance(value, (int, float)):
    return {"N": str(value)}
  elif isinstance(value, list):
    return {"L": [convert_to_dynamo_type(item) for item in value]}
  elif isinstance(value, dict):
    return {"M": {k: convert_to_dynamo_type(v) for k, v in value.items()}}
  else:
    return {"S": str(value)}

def seed_table_from_json(client, json_file):
  print(f"\nProcessing file: {json_file}")

  with json_file.open() as f:
    data = json.load(f)

  target_env = os.environ.get("ENV", "local")
  table_name = data.get("TableName")
  if not table_name:
    table_name = f"zooby-{target_env}-{json_file.stem}"

  # Replace 'local' in table_name with target_env
  table_name = table_name.replace("local", target_env)

  partition_key = data.get("partitionKey", "serial_number")
  items = data.get("Items") or data.get("items") or data

  if not isinstance(items, list):
    print(f"No valid items found in {json_file}")
    return

  print(f"Table name: {table_name}")
  print(f"Partition key: {partition_key}")
  print(f"Number of items: {len(items)}")

  for item in items:
    if not isinstance(item, dict):
      continue

    try:
      if partition_key not in item:
        print(f"Skipping - Missing partition key {partition_key} in item: {item}")
        continue

      dynamo_item = {k: convert_to_dynamo_type(v) for k, v in item.items()}

      print(f"\nAttempting to insert item:")
      print(f"Table: {table_name}")
      print(f"Item: {dynamo_item}")

      client.put_item(
        TableName=table_name,
        Item=dynamo_item
      )
      print(f"Success: inserted item with {partition_key}={item[partition_key]}")
    except Exception as e:
      print(f"Failed to insert item: {item}. Error: {str(e)}")

def main():
  schema_dir = Path(__file__).parent / "dynamodb"
  client = get_dynamodb_client()
  for json_file in schema_dir.glob("*.json"):
    print(f"Seeding from {json_file}")
    seed_table_from_json(client, json_file)

if __name__ == "__main__":
  main()
