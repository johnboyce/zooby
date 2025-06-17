package com.zooby.repository;

import com.zooby.model.ZoobyModel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ZoobyModelRepositoryTest {

    @Inject
    ZoobyModelRepository zoobyModelRepository;

    @Test
    void testSaveAndFindByModel() {
        // Arrange
        ZoobyModel model = new ZoobyModel();
        model.setModel("test-model");
        model.setName("Test Model");
        model.setDescription("Test Description");

        // Act
        zoobyModelRepository.save(model);
        Optional<ZoobyModel> retrievedModel = zoobyModelRepository.findByModel("test-model");

        // Assert
        assertTrue(retrievedModel.isPresent());
        assertEquals("Test Model", retrievedModel.get().getName());
        assertEquals("Test Description", retrievedModel.get().getDescription());
    }
}
