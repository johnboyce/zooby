package com.zooby.graphql;

import com.zooby.model.InventoryItem;
import com.zooby.service.InventoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@GraphQLApi
public class InventoryResource {

    private static final Logger logger = LoggerFactory.getLogger(InventoryResource.class);

    @Inject
    InventoryService inventoryService;

    @RolesAllowed("user")
    @Query("inventoryItems")
    @Description("List inventory items with pagination and optional filtering")
    public List<InventoryItem> listInventoryItems(
        @Name("filter") String filter,
        @Name("offset") @DefaultValue("0") int offset,
        @Name("limit") @DefaultValue("10") int limit
    ) {
        logger.debug("listInventoryItems called with filter: {}, offset: {}, limit: {}", filter, offset, limit);

        if (offset < 0) {
            logger.warn("Offset is negative: {}", offset);
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit < 1) {
            logger.warn("Limit is not positive: {}", limit);
            throw new IllegalArgumentException("Limit must be positive");
        }

        List<InventoryItem> results = inventoryService.findAll(filter, offset, limit);
        logger.info("listInventoryItems returned {} results", results.size());
        return results;
    }
}
