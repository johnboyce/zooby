package com.zooby.config;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class HeartbeatLogger {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatLogger.class);

    @Scheduled(every = "30s") // logs every 30 seconds
    void logHeartbeat() {
        LOG.info("💓 Heartbeat: Zooby backend is alive");
    }
}
