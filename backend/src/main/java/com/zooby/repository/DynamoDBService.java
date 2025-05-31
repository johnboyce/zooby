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

        if (getActivationStatus(txnId) == null) {
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
            LOG.info("Seeded activation status for " + txnId);
        }
    }


    public Map<String, AttributeValue> getActivationStatus(String transactionId) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("transactionId-index")
                .keyConditionExpression("transactionId = :txn")
                .expressionAttributeValues(Map.of(":txn", AttributeValue.fromS(transactionId)))
                .build();

        QueryResponse response = dynamoDb.query(queryRequest);
        return response.count() > 0 ? response.items().get(0) : null;
    }

    public void writeActivationStatus(Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDb.putItem(request);
    }
}
