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

    @Query("model")
    @Description("Lookup a Zooby model by model number")
    public Optional<ZoobyModel> getModel(@Name("model") String model) {
        return modelService.findByModel(model);
    }

    @Query("models")
    @Description("List all Zooby models with pagination and optional filtering")
    public List<ZoobyModel> listModels(
        @Name("filter") String filter,
        @Name("offset") @DefaultValue("0") int offset,
        @Name("limit") @DefaultValue("10") int limit
    ) {
        return modelService.findAll(filter, offset, limit);
    }
}
