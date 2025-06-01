package com.zooby.service;

import com.zooby.model.ZoobyModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZoobyModelService {

    @Inject
    DynamoDbClient dynamoDbClient;

    @ConfigProperty(name = "zooby.models.table")
    String tableName;

    public Optional<ZoobyModel> findByModel(String modelKey) {
        Map<String, AttributeValue> key = Map.of(
            "model", AttributeValue.builder().s(modelKey).build()
        );

        var response = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build()
        );

        return Optional.ofNullable(response.item())
            .filter(item -> !item.isEmpty())
            .map(ZoobyModel::from);
    }

    public List<ZoobyModel> findAll(String filter, int offset, int limit) {
        var items = dynamoDbClient.scan(
            ScanRequest.builder()
                .tableName(tableName)
                .build()
        ).items();

        return items.stream()
            .map(ZoobyModel::from)
            .filter(m -> filter == null
                || m.getName().toLowerCase().contains(filter.toLowerCase()))
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }
}
