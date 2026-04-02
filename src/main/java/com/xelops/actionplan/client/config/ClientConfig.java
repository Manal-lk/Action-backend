package com.xelops.actionplan.client.config;

import com.xelops.actionplan.service.KeycloakRealmService;
import feign.RequestInterceptor;
import feign.Retryer;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.Map;
import java.util.Optional;

@Configuration
@EnableFeignClients(basePackages = "com.xelops.actionplan.client")
@RequiredArgsConstructor
public class ClientConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final Tracer tracer;
    private final KeycloakRealmService keycloakRealmService;

    private final Map<String, String> clientRegistrationProviderMap = Map.of(
            "notification-client", "notification"
    );

    @Bean
    RequestInterceptor jwtRequestInterceptor() {
        return template -> {
            final var currentRealm = keycloakRealmService.getRealmForFeignClient();

            var registrationId = Optional.ofNullable(clientRegistrationProviderMap.get(template.feignTarget().name()))
                    .orElseThrow(() -> new IllegalArgumentException("No registration id found for client: " + template.feignTarget().name()));

            final var realmSpecificRegistrationId = registrationId + "-" + currentRealm;
            var clientRegistration = clientRegistrationRepository.findByRegistrationId(realmSpecificRegistrationId);
            if (clientRegistration != null) {
                var oAuth2ClientTokenManager = new OAuth2ClientTokenManager(authorizedClientManager(), clientRegistration);

                // Add authorization header
                template.header(HttpHeaders.AUTHORIZATION, GlobalConstants.HEADER_BEARER + oAuth2ClientTokenManager.getAccessToken());
            }

            // Add tracing headers
            var span = tracer.currentSpan();
            if (span != null) {
                var context = span.context();
                template.header("X-B3-TraceId", context.traceId());
                template.header("X-B3-SpanId", context.spanId());
            }
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    @ConditionalOnMissingBean
    OAuth2AuthorizedClientManager authorizedClientManager() {
        var tokenResponseClient = new RestClientClientCredentialsTokenResponseClient();
        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(c -> c.accessTokenResponseClient(tokenResponseClient))
                .build();
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }
}

