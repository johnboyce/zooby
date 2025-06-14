package com.zooby.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class HeartbeatLogger {

    private static final Logger LOG = Logger.getLogger(HeartbeatLogger.class);

    @Scheduled(every = "30s") // logs every 30 seconds
    void logHeartbeat() {
        LOG.info("💓 Heartbeat: Zooby backend is alive");
    }
}
