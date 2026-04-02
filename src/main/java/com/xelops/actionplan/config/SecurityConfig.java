package com.xelops.actionplan.config;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.enumeration.KeycloakUserAttributeEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserHelperService;
import com.xelops.actionplan.utils.security.AuthenticationFactory;
import com.xelops.actionplan.utils.security.JwtUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.xelops.actionplan.utils.constants.GlobalConstants.ROLE_PREFIX;
import static com.xelops.actionplan.utils.constants.GlobalConstants.SCOPE_PREFIX;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@EnableWebSocketSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakRealmProperties.class)
public class SecurityConfig {

    private final KeycloakRealmProperties keycloakRealmProperties;
    private final UserHelperService userHelperService;

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpSubscribeDestMatchers("/user/queue/error").permitAll()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**").denyAll()
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/user/**").authenticated()
                .simpDestMatchers("/topic/**", "/queue/**").denyAll()
                .simpTypeMatchers(
                        SimpMessageType.CONNECT,
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.MESSAGE,
                        SimpMessageType.SUBSCRIBE,
                        SimpMessageType.UNSUBSCRIBE
                ).authenticated()
                .anyMessage().denyAll();

        return messages.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder dynamicJwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/ws/**", "/actuator/health")
                        .permitAll()
                        .requestMatchers("/**").authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        .decoder(dynamicJwtDecoder)));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return new DynamicIssuerJwtDecoder(keycloakRealmProperties);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
        provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticationFactory authenticationFactory() {
        return BearerTokenAuthenticationToken::new;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomAuthoritiesConverter());
        return authenticationConverter;
    }

    static class DynamicIssuerJwtDecoder implements JwtDecoder {
        private final ConcurrentMap<String, JwtDecoder> decoders = new ConcurrentHashMap<>();
        private final Map<String, RealmConfig> realmConfigMap;

        DynamicIssuerJwtDecoder(KeycloakRealmProperties props) {
            this.realmConfigMap = props == null ? Collections.emptyMap() : props.getNormalizedRealmMap();
        }

        @Override
        public Jwt decode(String token) throws JwtException {
            Optional<String> issuerOpt = JwtUtils.getIssuerFromToken(token);
            if (issuerOpt.isEmpty()) {
                throw new JwtException("Unable to extract issuer (iss) from token");
            }
            String issuer = issuerOpt.get();
            String normalizedIssuer = KeycloakRealmProperties.normalize(issuer);

            RealmConfig realm = realmConfigMap.get(normalizedIssuer);
            if (realm == null) {
                throw new JwtException("Unknown or unconfigured issuer: " + issuer);
            }

            String jwksUri = realm.getJwkSetUri();
            String expectedIssuer = realm.getIssuerUri();
            String normalizedExpectedIssuer = KeycloakRealmProperties.normalize(expectedIssuer);

            try {
                JwtDecoder decoder = decoders.computeIfAbsent(jwksUri, uri -> {
                    NimbusJwtDecoder nimbus = NimbusJwtDecoder.withJwkSetUri(uri).build();
                    nimbus.setJwtValidator(JwtValidators.createDefaultWithIssuer(normalizedExpectedIssuer));
                    return nimbus;
                });
                return decoder.decode(token);
            } catch (Exception ex) {
                throw new JwtException("Failed to decode token for jwksUri=" + jwksUri + ": " + ex.getMessage(), ex);
            }
        }
    }

    @RequiredArgsConstructor
    class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        private static final Map<String, String> CLAIMS_TO_AUTHORITY_PREFIX_MAP = Map.of(
                "roles", ROLE_PREFIX,
                "scope", SCOPE_PREFIX
        );

        @Override
        public Collection<GrantedAuthority> convert(@NotNull Jwt jwt) {
            List<GrantedAuthority> mappedAuthorities;
            // Convert JWT claims to GrantedAuthority objects based on the CLAIMS_TO_AUTHORITY_PREFIX_MAP
            mappedAuthorities = CLAIMS_TO_AUTHORITY_PREFIX_MAP.entrySet().stream()
                    .map(entry -> getAuthorities(jwt, entry.getKey(), entry.getValue()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(ArrayList::new));

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                roles.forEach(r -> mappedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + r.toString())));
            }
            User existingUser = null;
            String keycloakId = jwt.getClaim(KeycloakUserAttributeEnum.KEYCLOAK_ID.getAttribute());
            try {
                existingUser = userHelperService.findUserByKeycloakId(keycloakId);
            } catch (NotFoundException ignored) {
                // Intentionally ignoring the exception because user not found is an expected case
            }
            if (existingUser != null) {
                mappedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + existingUser.getRole()));
            }

            return mappedAuthorities;
        }

        private Collection<GrantedAuthority> getAuthorities(Jwt jwt, String authorityClaimName, String authorityPrefix) {
            Object authorities = jwt.getClaim(authorityClaimName);
            if (authorities == null) return List.of();
            Collection<String> claimList = authorities instanceof Collection ? (Collection<String>) authorities :
                    Arrays.asList(((String) authorities).split(" "));
            return claimList.stream()
                    .filter(StringUtils::isNotEmpty)
                    .map(claim -> new SimpleGrantedAuthority(authorityPrefix + claim))
                    .collect(Collectors.toList());
        }

    }
}
