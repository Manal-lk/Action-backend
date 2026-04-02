package com.xelops.actionplan.utils.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

public final class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private JwtUtils() {
    }

    public static Optional<String> getIssuerFromToken(String rawToken) {
        if (rawToken == null) return Optional.empty();
        String token = stripBearerPrefix(rawToken.trim());
        if (token.isEmpty()) return Optional.empty();

        try {
            JWT jwt = JWTParser.parse(token);
            String issuer = jwt.getJWTClaimsSet().getIssuer();
            return Optional.ofNullable(issuer).filter(s -> !s.isBlank());
        } catch (ParseException e) {
            log.info("Failed to parse JWT while extracting issuer: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.info("Unexpected error while extracting issuer from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static String stripBearerPrefix(String token) {
        if (token.toLowerCase().startsWith("bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }
}

