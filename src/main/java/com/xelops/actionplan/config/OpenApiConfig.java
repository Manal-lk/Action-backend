package com.xelops.actionplan.config;


import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static SecurityScheme getSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat(GlobalConstants.BEARER_FORMAT)
                .scheme(GlobalConstants.SCHEME);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes(GlobalConstants.SECURITY_SCHEME_NAME, getSecurityScheme()));
    }

}
