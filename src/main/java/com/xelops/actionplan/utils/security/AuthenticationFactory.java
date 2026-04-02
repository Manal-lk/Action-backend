package com.xelops.actionplan.utils.security;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface AuthenticationFactory {
    Authentication getAuthentication(String token);
}
