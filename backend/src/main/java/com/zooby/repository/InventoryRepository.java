package com.zooby.repository;


import com.zooby.model.InventoryItem;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.enhanced.dynamodb.*;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InventoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<InventoryItem> table;

    public InventoryRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.table = enhancedClient.table("zooby-local-inventory", TableSchema.fromBean(InventoryItem.class));
    }

    public List<InventoryItem> listAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
}
