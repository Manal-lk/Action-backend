package com.xelops.actionplan.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.Map;

@UtilityClass
public class JWTUtility {

    public static Map<String, Object> getClaims() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            return Collections.emptyMap();
        }
        if (jwt.getClaims() == null) {
            return Collections.emptyMap();
        }
        return jwt.getClaims();
    }

    public static String getClaim(@NotNull String claimKey) {
        return (String) getClaims().get(claimKey);
    }
}
