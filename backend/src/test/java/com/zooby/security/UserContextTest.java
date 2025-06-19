package com.zooby.security;

import com.zooby.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.MDC;

import java.security.Principal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserContextTest {

    UserContext userContext;
    UserService mockUserService;
    SecurityIdentity mockIdentity;

    @BeforeEach
    void setup() {
        mockUserService = mock(UserService.class);
        mockIdentity = mock(SecurityIdentity.class);
        userContext = new UserContext();
        userContext.userService = mockUserService;
        userContext.identity = mockIdentity;
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void testInit_withProviderAndRoles() {
        when(mockIdentity.getPrincipal()).thenReturn(() -> "user123");
        when(mockIdentity.getAttribute("provider")).thenReturn("auth0");
        when(mockIdentity.getAttribute("provider_id")).thenReturn("abc-123");
        when(mockUserService.getRoles("auth0", "abc-123")).thenReturn(Set.of("user"));

        userContext.init();

        verify(mockUserService).createUserIfMissing("auth0", "abc-123", "user123");
        assertEquals("user123", userContext.getUserId());
        assertTrue(userContext.getRoles().contains("user"));
        assertEquals("user123", MDC.get("userId"));
        assertEquals("none", MDC.get("account")); // account is null in this case
        assertEquals("[user]", MDC.get("roles"));
    }

    @Test
    void testInit_missingProviderSkipsRoleFetch() {
        when(mockIdentity.getPrincipal()).thenReturn(() -> "anon");

        userContext.init();

        assertEquals("anon", userContext.getUserId());
        assertEquals(Set.of(), userContext.getRoles());
        assertEquals("anon", MDC.get("userId"));
        assertEquals("none", MDC.get("account"));
        assertEquals("[]", MDC.get("roles"));
    }
}
