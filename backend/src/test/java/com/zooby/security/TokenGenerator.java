package com.zooby.security;

import io.smallrye.jwt.build.Jwt;
import java.util.HashSet;
import java.util.Set;

public class TokenGenerator {

    public static String generate(String userId, String... capabilities) {
        Set<String> caps = new HashSet<>();
        for (String c : capabilities) {
            caps.add(c);
        }

        return Jwt.claim("userId", userId)
            .claim("capabilities", caps)
            .sign();
    }
}
