package com.zooby.service;

import com.zooby.model.ZoobyModel;
import com.zooby.repository.ZoobyModelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ZoobyModelService {

    private static final Logger LOG = LoggerFactory.getLogger(ZoobyModelService.class);
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
