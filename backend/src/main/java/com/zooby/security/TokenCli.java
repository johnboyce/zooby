package com.zooby.security;

public class TokenCli {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: TokenCli <userId> [role] [capability1 capability2 ...]");
            System.exit(1);
        }

        String userId = args[0];
        String role = args.length > 1 ? args[1] : "customer";
        String[] capabilities = args.length > 2 ? java.util.Arrays.copyOfRange(args, 2, args.length) : new String[0];

        String token = TokenGenerator.generate(userId, role, capabilities);

        System.out.println("Generated JWT:\n\n" + token);
    }
}
