package com.xelops.actionplan.config;

import com.xelops.actionplan.service.KeycloakRealmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DynamicOAuth2ClientConfig {

    private final KeycloakRealmService keycloakRealmService;

    @Bean
    @Primary
    public ClientRegistrationRepository dynamicClientRegistrationRepository() {
        Map<String, ClientRegistration> registrations = new HashMap<>();

        for (final var realmName : keycloakRealmService.getAvailableRealms()) {
            final var realm = keycloakRealmService.getRealmConfig(realmName);
            for (ClientConfig client : realm.getClients()) {
                String registrationId = client.getName() + "-" + realm.getName();

                registrations.put(
                        registrationId,
                        createClientRegistration(realm, client, registrationId)
                );

                log.info("Registered OAuth2 client [{}]", registrationId);
            }
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration createClientRegistration(
            RealmConfig realm,
            ClientConfig client,
            String registrationId
    ) {
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(realm.getIssuerUri() + "/protocol/openid-connect/token")
                .build();
    }
}
