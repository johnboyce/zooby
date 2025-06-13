package com.zooby.config;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Startup
@Singleton
public class StartupLogger {

    private static final Logger LOG = Logger.getLogger(StartupLogger.class);

    @ConfigProperty(name = "quarkus.profile")
    String profile;

    @ConfigProperty(name = "zooby.models.table")
    String modelsTable;

    @ConfigProperty(name = "zooby.inventory.table")
    String inventoryTable;

    @ConfigProperty(name = "quarkus.dynamodb.aws.credentials.type")
    String credentialsType;

    @PostConstruct
    void logSettings() {
        LOG.info("🔍 Startup settings logged at " + java.time.Instant.now());
        LOG.info("🚀 Starting with profile: " + profile);
        LOG.info("📦 Models Table: " + modelsTable);
        LOG.info("📦 Inventory Table: " + inventoryTable);
        LOG.info("🔐 Credentials Type: " + credentialsType);
    }
}

