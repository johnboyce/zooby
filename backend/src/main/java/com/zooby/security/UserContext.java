package com.zooby.security;

import io.opentelemetry.api.trace.Span;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.zooby.service.UserService;

import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class UserContext {

    private static final Logger LOG = LoggerFactory.getLogger(UserContext.class);

    @Inject
    SecurityIdentity identity;

    @Inject
    UserService userService;

    private String userId;
    private String account;
    private Set<String> roles = new HashSet<>();

    @PostConstruct
    void init() {
        this.userId = identity.getPrincipal().getName();
        String provider = identity.getAttribute("provider");
        String providerId = identity.getAttribute("provider_id");

        if (provider != null && providerId != null) {
            // Ensure user exists, and load roles
            userService.createUserIfMissing(provider, providerId, userId);
            this.roles = userService.getRoles(provider, providerId);
        }

        MDC.put("traceId", Span.current().getSpanContext().getTraceId());
        MDC.put("spanId", Span.current().getSpanContext().getSpanId());
        MDC.put("userId", userId != null ? userId : "anonymous");
        MDC.put("account", account != null ? account : "none");
        MDC.put("roles", roles.toString());
    }

    public String getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasCapability(String capability) {
        Set<String> caps = identity.getAttribute("capabilities");
        return caps != null && caps.contains(capability);
    }

    @PreDestroy
    void cleanup() {
        MDC.clear();
    }
}
