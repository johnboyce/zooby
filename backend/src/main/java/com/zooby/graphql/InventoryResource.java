package com.zooby.graphql;

import com.zooby.model.InventoryItem;
import com.zooby.service.InventoryService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class InventoryResource {
    @Inject
    InventoryService inventoryService;


    @Query("inventoryItems")
    @Description("List inventory items with pagination and optional filtering")
    public List<InventoryItem> listInventoryItems(
        @Name("filter") String filter,
        @Name("offset") @DefaultValue("0") int offset,
        @Name("limit") @DefaultValue("10") int limit
    ) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return inventoryService.findAll(filter, offset, limit);
    }

}
