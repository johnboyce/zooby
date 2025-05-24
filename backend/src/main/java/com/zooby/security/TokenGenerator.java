package com.zooby.security;

import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class TokenGenerator {

    public static String generate(String userId, String... capabilities) {
        return generate(userId, "customer", capabilities); // default to "customer"
    }

    public static String generate(String userId, String role, String... capabilities) {
        Set<String> caps = new HashSet<>(Set.of(capabilities));
        Set<String> groups = Set.of(role); // roles: "customer", "manager", "admin"

        return Jwt.issuer("https://auth.zooby.dev")
            .subject(userId)
            .groups(groups)
            .claim("userId", userId)
            .claim("capabilities", caps)
            .expiresIn(Duration.ofHours(1))
            .sign();
    }
}
