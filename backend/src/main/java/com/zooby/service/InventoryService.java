package com.zooby.service;

import com.zooby.model.InventoryItem;
import com.zooby.repository.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    @Inject
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Optional<InventoryItem> findBySerialNumber(String serialNumber) {
        return inventoryRepository.find("serialNumber", serialNumber);
    }

    public List<InventoryItem> findAll(String filter, int offset, int limit) {
        if (filter == null) {
            return inventoryRepository.findAll().stream()
                .skip(offset)
                .limit(limit)
                .toList();
        }
        return inventoryRepository.find("serialNumber", filter, offset, limit);
    }
}
