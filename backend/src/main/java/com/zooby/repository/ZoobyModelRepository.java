package com.zooby.repository;


import com.zooby.model.ZoobyModel;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.enhanced.dynamodb.*;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZoobyModelRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<ZoobyModel> table;

    public ZoobyModelRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.table = enhancedClient.table("zooby-local-models", TableSchema.fromBean(ZoobyModel.class));
    }

    public List<ZoobyModel> listAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
    // implement save

}
