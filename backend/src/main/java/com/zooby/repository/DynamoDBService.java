package com.zooby.repository;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class DynamoDBService {

    private static final Logger LOG = Logger.getLogger(DynamoDBService.class);
    private final String tableName = "zooby_activation";

    @Inject
    DynamoDbClient dynamoDb;

    @PostConstruct
    void seedIfEmpty() {
        String txnId = "txn-AABBCCDDEE00-1716515612";
        LOG.debugf("Checking if activation status exists for transaction ID: %s", txnId);

        try {
            if (getActivationStatus(txnId) == null) {
                LOG.infof("No activation status found for transaction ID: %s. Seeding data.", txnId);
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
                LOG.infof("Seeded activation status for transaction ID: %s", txnId);
            } else {
                LOG.debugf("Activation status already exists for transaction ID: %s", txnId);
            }
        } catch (Exception e) {
            LOG.errorf("Error during seed operation for transaction ID: %s. Message: %s", txnId, e.getMessage());
        }
    }

    public Map<String, AttributeValue> getActivationStatus(String transactionId) {
        LOG.debugf("Fetching activation status for transaction ID: %s", transactionId);
        try {
            QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("transactionId-index")
                .keyConditionExpression("transactionId = :txn")
                .expressionAttributeValues(Map.of(":txn", AttributeValue.fromS(transactionId)))
                .build();

            QueryResponse response = dynamoDb.query(queryRequest);
            if (response.count() > 0) {
                LOG.infof("Activation status found for transaction ID: %s", transactionId);
                return response.items().get(0);
            } else {
                LOG.warnf("No activation status found for transaction ID: %s", transactionId);
                return null;
            }
        } catch (DynamoDbException e) {
            LOG.errorf("DynamoDB error while fetching activation status for transaction ID: %s. Message: %s", transactionId, e.getMessage());
            return null;
        } catch (Exception e) {
            LOG.errorf("Unexpected error while fetching activation status for transaction ID: %s. Message: %s", transactionId, e.getMessage());
            return null;
        }
    }

    public void writeActivationStatus(Map<String, AttributeValue> item) {
        String transactionId = item.get("transactionId").s();
        LOG.debugf("Writing activation status for transaction ID: %s", transactionId);
        try {
            PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

            dynamoDb.putItem(request);
            LOG.infof("Activation status written successfully for transaction ID: %s", transactionId);
        } catch (DynamoDbException e) {
            LOG.errorf("DynamoDB error while writing activation status for transaction ID: %s. Message: %s", transactionId, e.getMessage());
        } catch (Exception e) {
            LOG.errorf("Unexpected error while writing activation status for transaction ID: %s. Message: %s", transactionId, e.getMessage());
        }
    }
}
