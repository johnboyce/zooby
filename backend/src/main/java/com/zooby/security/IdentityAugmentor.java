package com.zooby.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.zooby.service.UserService;

import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class IdentityAugmentor implements SecurityIdentityAugmentor {

    @Inject
    UserService userService;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context, Map<String, Object> attributes) {
        String provider = identity.getAttribute("provider");
        String providerId = identity.getAttribute("provider_id");

        if (provider == null || providerId == null) {
            return Uni.createFrom().item(identity);
        }

        Set<String> roles = userService.getRoles(provider, providerId);

        return Uni.createFrom().item(
            QuarkusSecurityIdentity.builder(identity)
                .addRoles(roles)
                .build()
        );
    }

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        // fallback for older extensions that call this
        return Uni.createFrom().item(identity);
    }

    @Override
    public int priority() {
        return 10;
    }
}
