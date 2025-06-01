package com.zooby.repository;

import com.zooby.model.InventoryItem;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class InventoryRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public InventoryRepository(
            DynamoDbClient dynamoDbClient,
            @ConfigProperty(name = "zooby.inventory.table") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public List<InventoryItem> findAll() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        return response.items().stream()
                .map(this::mapToInventoryItem)
                .collect(Collectors.toList());
    }

    public Optional<InventoryItem> find(String indexName, String value) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(indexName, AttributeValue.builder().s(value).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.hasItem() ? Optional.of(mapToInventoryItem(response.item())) : Optional.empty();
    }

    public List<InventoryItem> find(String filter, String value, int offset, int limit) {
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
        return response.items().stream()
                .map(this::mapToInventoryItem)
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<InventoryItem> findByModel(String model, int offset, int limit) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":model", AttributeValue.builder().s(model).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("model = :model")
                .expressionAttributeValues(expressionValues)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        return response.items().stream()
                .map(this::mapToInventoryItem)
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void persist(InventoryItem item) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("serialNumber", AttributeValue.builder().s(item.getSerialNumber()).build());
        itemValues.put("model", AttributeValue.builder().s(item.getModel()).build());
        itemValues.put("macAddress", AttributeValue.builder().s(item.getMacAddress()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    private InventoryItem mapToInventoryItem(Map<String, AttributeValue> item) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setSerialNumber(item.get("serialNumber").s());
        inventoryItem.setModel(item.get("model").s());
        inventoryItem.setMacAddress(item.get("macAddress").s());
        return inventoryItem;
    }
}
