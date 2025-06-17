package com.zooby.security;

import io.opentelemetry.api.trace.Span;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.slf4j.MDC;

import java.util.Set;

@RequestScoped
public class UserContext {

    @Inject
    SecurityIdentity identity;

    private String userId;
    private String account;
    private Set<String> roles;

    @PostConstruct
    void init() {
        this.userId = identity.getPrincipal().getName();
        this.account = identity.getAttribute("account");
        this.roles = identity.getRoles();
        MDC.put("traceId", Span.current().getSpanContext().getTraceId());
        MDC.put("spanId", Span.current().getSpanContext().getSpanId());
        MDC.put("userId", userId != null ? userId : "anonymous");
        MDC.put("account", account != null ? account : "none");
        MDC.put("roles", roles != null ? roles.toString() : "[]");
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
