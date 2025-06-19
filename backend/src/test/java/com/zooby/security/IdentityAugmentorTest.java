package com.zooby.security;

import com.zooby.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdentityAugmentorTest {

    @Test
    void testAugment_skipsWhenNoProviderInfo() {
        var userService = mock(UserService.class);
        var augmentor = new IdentityAugmentor();
        augmentor.userService = userService;

        var identity = QuarkusSecurityIdentity.builder()
            .setPrincipal(() -> "john")
            .build();

        Uni<SecurityIdentity> result = augmentor.augment(identity, null, Map.of());
        assertEquals(identity, result.await().indefinitely());
    }

    @Test
    void testAugment_addsRolesFromUserService() {
        var userService = mock(UserService.class);
        when(userService.getRoles("google", "abc123")).thenReturn(Set.of("admin", "user"));

        var identity = QuarkusSecurityIdentity.builder()
            .setPrincipal(() -> "john")
            .addAttribute("provider", "google")
            .addAttribute("provider_id", "abc123")
            .build();

        var augmentor = new IdentityAugmentor();
        augmentor.userService = userService;

        var result = augmentor.augment(identity, null, Map.of()).await().indefinitely();

        assertTrue(result.getRoles().contains("admin"));
        assertTrue(result.getRoles().contains("user"));
        assertEquals("john", result.getPrincipal().getName());
    }
}
