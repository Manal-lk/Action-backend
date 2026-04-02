package com.xelops.actionplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakRealmProperties {

    private List<RealmConfig> realms;

    public Map<String, RealmConfig> getNormalizedRealmMap() {
        if (realms == null) return Collections.emptyMap();
        return realms.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getIssuerUri() != null && e.getJwkSetUri() != null)
                .collect(Collectors.toUnmodifiableMap(e -> normalize(e.getIssuerUri()), e -> e, (a, b) -> a));
    }

    public static String normalize(String issuerUri) {
        if (issuerUri == null) return null;
        String normalizedUri = issuerUri.trim();
        if (normalizedUri.endsWith("/")) {
            normalizedUri = normalizedUri.substring(0, normalizedUri.length() - 1);
        }
        return normalizedUri;
    }
}
