package com.zooby.repository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DynamoDBService {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBService.class);
    private final String tableName = "zooby_activation";

    @Inject
    DynamoDbClient dynamoDb;

    @PostConstruct
    void seedIfEmpty() {
        String txnId = "txn-AABBCCDDEE00-1716515612";
        LOG.debug("Checking if activation status exists for transaction ID: {}", txnId);

        try {
            if (getActivationStatus(txnId) == null) {
                LOG.info("No activation status found for transaction ID: {}. Seeding data.", txnId);
                Map<String, AttributeValue> item = new HashMap<>();
                item.put("userId", AttributeValue.fromS("user-123"));
                item.put("transactionId", AttributeValue.fromS(txnId));
                item.put("macAddress", AttributeValue.fromS("AA:BB:CC:DD:EE:00"));
                item.put("status", AttributeValue.fromS("INPROGRESS"));
                item.put("stepsLog", AttributeValue.fromSs(
                    List.of("Initializing", "Contacting Zoomba", "Bootfile Ready")
                ));
                item.put("updatedAt", AttributeValue.fromS(Instant.now().toString()));

                writeActivationStatus(item);
                LOG.info("Seeded activation status for transaction ID: {}", txnId);
            } else {
                LOG.debug("Activation status already exists for transaction ID: {}", txnId);
            }
        } catch (Exception e) {
            LOG.error("Error during seed operation for transaction ID: {}. Message: {}", txnId, e.getMessage());
        }
    }

    public Map<String, AttributeValue> getActivationStatus(String transactionId) {
        LOG.debug("Fetching activation status for transaction ID: {}", transactionId);
        try {
            QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("transactionId-index")
                .keyConditionExpression("transactionId = :txn")
                .expressionAttributeValues(Map.of(":txn", AttributeValue.fromS(transactionId)))
                .build();

            QueryResponse response = dynamoDb.query(queryRequest);
            if (response.count() > 0) {
                LOG.info("Activation status found for transaction ID: {}", transactionId);
                return response.items().get(0);
            } else {
                LOG.warn("No activation status found for transaction ID: {}", transactionId);
                return null;
            }
        } catch (DynamoDbException e) {
            LOG.error("DynamoDB error while fetching activation status for transaction ID: {}. Message: {}", transactionId, e.getMessage());
            return null;
        } catch (Exception e) {
            LOG.error("Unexpected error while fetching activation status for transaction ID: {}. Message: {}", transactionId, e.getMessage());
            return null;
        }
    }

    public void writeActivationStatus(Map<String, AttributeValue> item) {
        String transactionId = item.get("transactionId").s();
        LOG.debug("Writing activation status for transaction ID: {}", transactionId);
        try {
            PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

            dynamoDb.putItem(request);
            LOG.info("Activation status written successfully for transaction ID: {}", transactionId);
        } catch (DynamoDbException e) {
            LOG.error("DynamoDB error while writing activation status for transaction ID: {}. Message: {}", transactionId, e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error while writing activation status for transaction ID: {}. Message: {}", transactionId, e.getMessage());
        }
    }
}
