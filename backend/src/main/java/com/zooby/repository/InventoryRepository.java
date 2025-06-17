package com.zooby.repository;

import com.zooby.model.InventoryItem;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class InventoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(InventoryRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public InventoryRepository(
        DynamoDbClient dynamoDbClient,
        @ConfigProperty(name = "zooby.inventory.table") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        logger.info("InventoryRepository initialized with table: {}", tableName);
    }

    public List<InventoryItem> findAll() {
        logger.debug("findAll called");
        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(tableName)
            .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        logger.info("findAll returned {} items", response.items().size());
        return response.items().stream()
            .map(this::mapToInventoryItem)
            .collect(Collectors.toList());
    }

    public Optional<InventoryItem> find(String indexName, String value) {
        logger.debug("find called with indexName: {}, value: {}", indexName, value);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(indexName, AttributeValue.builder().s(value).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        if (response.hasItem()) {
            logger.info("Item found for indexName: {}, value: {}", indexName, value);
            return Optional.of(mapToInventoryItem(response.item()));
        } else {
            logger.warn("No item found for indexName: {}, value: {}", indexName, value);
            return Optional.empty();
        }
    }

    public List<InventoryItem> find(String filter, String value, int offset, int limit) {
        logger.debug("find called with filter: {}, value: {}, offset: {}, limit: {}", filter, value, offset, limit);
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#attr", filter);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":value", AttributeValue.builder().s(value).build());

        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(tableName)
            .filterExpression("contains(#attr, :value)")
            .expressionAttributeNames(expressionNames)
            .expressionAttributeValues(expressionValues)
            .build();

        ScanResponse response = dynamoDbClient.scan(scanRequest);
        logger.info("find returned {} items", response.items().size());
        return response.items().stream()
            .map(this::mapToInventoryItem)
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<InventoryItem> findByModel(String model, int offset, int limit) {
        logger.debug("findByModel called with model: {}, offset: {}, limit: {}", model, offset, limit);
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":model", AttributeValue.builder().s(model).build());

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("model = :model")
            .expressionAttributeValues(expressionValues)
            .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        logger.info("findByModel returned {} items", response.items().size());
        return response.items().stream()
            .map(this::mapToInventoryItem)
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

    public void persist(InventoryItem item) {
        logger.debug("persist called for item: {}", item.getSerialNumber());
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("serialNumber", AttributeValue.builder().s(item.getSerialNumber()).build());
        itemValues.put("model", AttributeValue.builder().s(item.getModel()).build());
        itemValues.put("macAddress", AttributeValue.builder().s(item.getMacAddress()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        dynamoDbClient.putItem(putItemRequest);
        logger.info("Item persisted successfully: {}", item.getSerialNumber());
    }

    private InventoryItem mapToInventoryItem(Map<String, AttributeValue> item) {
        logger.debug("Mapping DynamoDB item to InventoryItem");
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setSerialNumber(item.get("serialNumber").s());
        inventoryItem.setModel(item.get("model").s());
        inventoryItem.setMacAddress(item.get("macAddress").s());
        return inventoryItem;
    }
}
