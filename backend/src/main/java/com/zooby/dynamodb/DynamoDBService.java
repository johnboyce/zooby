package com.zooby.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.*;

@ApplicationScoped
public class DynamoDBService {

    private final String tableName = "zooby_activation";
    private final DynamoDbClient client;

    public DynamoDBService() {
        client = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();
    }

    public Map<String, AttributeValue> getActivationStatus(String transactionId) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .indexName("transactionId-index")
            .keyConditionExpression("transactionId = :txn")
            .expressionAttributeValues(Map.of(":txn", AttributeValue.fromS(transactionId)))
            .build();

        QueryResponse response = client.query(queryRequest);
        if (response.count() > 0) {
            return response.items().get(0);
        } else {
            return null;
        }
    }

    public void writeActivationStatus(Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();

        client.putItem(request);
    }
}
