package com.zooby.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.MDC;

import java.util.Optional;
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

        MDC.put("userId", userId);
        MDC.put("account", account);
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
}
