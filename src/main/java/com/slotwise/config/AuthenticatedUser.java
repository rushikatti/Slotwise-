package com.slotwise.config;

public record AuthenticatedUser(Long userId, Long tenantId, String email, String role) {}
