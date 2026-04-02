package com.xelops.actionplan.service;

import com.xelops.actionplan.config.KeycloakRealmProperties;
import com.xelops.actionplan.config.RealmConfig;
import com.xelops.actionplan.utils.JWTUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakRealmService {

    private final KeycloakRealmProperties keycloakRealmProperties;

    private static final String REALM_STRING = "/realms/";

    public String getRealmForFeignClient() {
        log.info("Start Service getRealmForFeignClient");
        final var currentRealm = getCurrentRealm(JWTUtility.getClaims());
        log.info("End Service getRealmForFeignClient | current realm: {}", currentRealm);
        return currentRealm;
    }

    public String getCurrentRealm(Map<String, Object> claims) {
        log.info("Start Service getCurrentRealm | claims: {}", claims);

        try {
            final var realmFromToken = extractRealmFromClaims(claims);
            if (isValidRealm(realmFromToken)) {
                log.info("End Service getCurrentRealm | realm from token: {}", realmFromToken);
                return realmFromToken;
            }
        } catch (Exception e) {
            log.info("Could not extract realm from token: {}", e.getMessage());
        }

        throw new IllegalStateException("No valid realm found in JWT token and no default realm configured");
    }

    private boolean isValidRealm(String realm) {
        if (realm == null) {
            return false;
        }

        return keycloakRealmProperties.getRealms().stream()
                .anyMatch(realmConfig -> realmConfig.getName().equals(realm));
    }

    private String extractRealmFromClaims(Map<String, Object> claims) {
        try {
            final var issuer = (String) claims.get("iss");

            if (issuer != null && issuer.contains(REALM_STRING)) {
                String realm = issuer.substring(issuer.lastIndexOf(REALM_STRING) + REALM_STRING.length());
                if (realm.contains("/")) {
                    realm = realm.substring(0, realm.indexOf("/"));
                }
                return realm;
            }
        } catch (Exception e) {
            log.info("Error extracting realm from token: {}", e.getMessage());
        }

        return null;
    }

    public List<String> getAvailableRealms() {
        log.info("Start Service getAvailableRealms");

        final var realms = keycloakRealmProperties.getRealms().stream()
                .map(RealmConfig::getName)
                .toList();

        log.info("End Service getAvailableRealms | realms: {}", realms);
        return realms;
    }

    public RealmConfig getRealmConfig(String realmName) {
        return keycloakRealmProperties.getRealms().stream()
                .filter(realm -> realm.getName().equals(realmName))
                .findFirst()
                .orElse(null);
    }
}