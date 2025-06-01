package com.zooby.graphql;

import com.zooby.model.ZoobyModel;
import com.zooby.service.ZoobyModelService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class ModelResource {
    @Inject
    ZoobyModelService modelService;

    @Query("zoobyModel")
    @Description("Lookup a Zooby model by model number")
    public Optional<ZoobyModel> getModel(
            @Name("model")
            @Description("The model number to look up")
            @NonNull String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be empty");
        }
        return modelService.findByModel(model);
    }

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
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return modelService.findAll(filter, offset, limit);
    }
}
