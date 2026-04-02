package com.xelops.actionplan.client.config;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Objects;

@RequiredArgsConstructor
public class OAuth2ClientTokenManager {
    private final OAuth2AuthorizedClientManager manager;
    private final ClientRegistration clientRegistration;


    public String getAccessToken() {
        Authentication principal = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        var authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(principal)
                .build();
        var client = manager.authorize(authorizeRequest);

        if (Objects.isNull(client)) {
            throw new IllegalStateException("Client Credentials for client: " + clientRegistration.getRegistrationId() + " is null");
        }
        if (Objects.isNull(client.getAccessToken())) {
            throw new IllegalStateException("Access token for client: " + clientRegistration.getRegistrationId() + " is null");
        }
        return client.getAccessToken()
                .getTokenValue();
    }

}