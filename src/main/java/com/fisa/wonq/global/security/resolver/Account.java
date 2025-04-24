package com.fisa.wonq.global.security.resolver;

public record Account(
        Long id,
        String userId,
        String role
) {
    public static Account of(Long id, String userId, String role) {
        return new Account(id, userId, role);
    }
}
