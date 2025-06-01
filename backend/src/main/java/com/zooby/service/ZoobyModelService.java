package com.zooby.service;

import com.zooby.model.ZoobyModel;
import com.zooby.repository.ZoobyModelRepository;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZoobyModelService {

    private static final Logger LOG = Logger.getLogger(ZoobyModelService.class);
    private final ZoobyModelRepository repository;

    @Inject
    public ZoobyModelService(ZoobyModelRepository repository) {
        this.repository = repository;
    }

    public Optional<ZoobyModel> findByModel(String modelKey) {
        return repository.findByModel(modelKey);
    }

    public List<ZoobyModel> findAll(String filter, int offset, int limit) {
        return repository.findAll(filter, offset, limit);
    }
}
