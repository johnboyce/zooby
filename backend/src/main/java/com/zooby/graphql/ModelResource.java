package com.zooby.graphql;

import com.zooby.model.ZoobyModel;
import com.zooby.service.ZoobyModelService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class ModelResource {
    private static final Logger logger = LoggerFactory.getLogger(ModelResource.class);

    @Inject
    ZoobyModelService modelService;

    @RolesAllowed("user")
    @Query("zoobyModel")
    @Description("Lookup a Zooby model by model number")
    public Optional<ZoobyModel> getModel(
        @Name("model")
        @Description("The model number to look up")
        @NonNull String model) {
        logger.debug("getModel called with model: {}", model);
        if (model == null || model.trim().isEmpty()) {
            logger.warn("Model number is empty or null");
            throw new IllegalArgumentException("Model number cannot be empty");
        }
        Optional<ZoobyModel> result = modelService.findByModel(model);
        logger.info("getModel returned: {}", result.isPresent() ? "Model found" : "No model found");
        return result;
    }

    @RolesAllowed("user")
    @Query("zoobyModels")
    @Description("List all Zooby models with pagination and optional filtering")
    public @NonNull List<ZoobyModel> listModels(
        @Name("filter")
        @Description("Optional text to filter models")
        String filter,

        @Name("offset")
        @Description("Number of items to skip")
        @DefaultValue("0") int offset,

        @Name("limit")
        @Description("Maximum number of items to return")
        @DefaultValue("10") int limit
    ) {
        logger.debug("listModels called with filter: {}, offset: {}, limit: {}", filter, offset, limit);
        if (offset < 0) {
            logger.warn("Offset is negative: {}", offset);
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit < 1) {
            logger.warn("Limit is not positive: {}", limit);
            throw new IllegalArgumentException("Limit must be positive");
        }
        List<ZoobyModel> results = modelService.findAll(filter, offset, limit);
        logger.info("listModels returned {} results", results.size());
        return results;
    }
}
