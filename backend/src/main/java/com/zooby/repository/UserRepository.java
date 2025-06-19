package com.zooby.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserRepository {

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "zooby.users.table")
    String tableName;

    public Set<String> getRoles(String provider, String providerId) {
        var item = getUserItem(provider, providerId);
        if (item == null || !item.containsKey("roles")) return Set.of();

        return item.get("roles").l().stream()
            .map(AttributeValue::s)
            .collect(Collectors.toSet());
    }

    public void createUserIfMissing(String provider, String providerId, String userId) {
        var key = Map.of(
            "provider", AttributeValue.fromS(provider),
            "provider_id", AttributeValue.fromS(providerId)
        );

        var getRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();

        var existing = dynamoDb.getItem(getRequest).item();
        if (existing != null && !existing.isEmpty()) return;

        var item = new HashMap<String, AttributeValue>();
        item.put("user_id", AttributeValue.fromS(userId));
        item.put("provider", AttributeValue.fromS(provider));
        item.put("provider_id", AttributeValue.fromS(providerId));
        item.put("roles", AttributeValue.fromL(Collections.emptyList()));

        var putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .conditionExpression("attribute_not_exists(provider)")
            .build();

        try {
            dynamoDb.putItem(putRequest);
        } catch (ConditionalCheckFailedException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    private Map<String, AttributeValue> getUserItem(String provider, String providerId) {
        var key = Map.of(
            "provider", AttributeValue.fromS(provider),
            "provider_id", AttributeValue.fromS(providerId)
        );

        var getRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();

        return dynamoDb.getItem(getRequest).item();
    }
}
