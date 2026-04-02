package com.xelops.actionplan.config;

import com.xelops.actionplan.dto.OrganizationDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.KeycloakRealmService;
import com.xelops.actionplan.service.UserHelperService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Import(SecurityConfig.class)
public class TestConfig {

    @MockBean
    Messages messages;

    @MockBean
    KeycloakRealmProperties keycloakRealmProperties;

    @MockBean
    KeycloakRealmService keycloakRealmService;


    @Bean
    public UserHelperService userHelperService() throws NotFoundException {
        UserHelperService securityHelper = mock(UserHelperService.class);

        when(securityHelper.getConnectedUserDetails()).thenReturn(UserPrivilegesDto.builder().userId(1L).keycloakId("test").username("john.doe").fullname("John Doe").email("john.doe@mail.com").role(UserRoleEnum.SIMPLE_USER).organization(OrganizationDto.builder().id(1L).build()).build());

        return securityHelper;
    }

    @Bean
    public Tracer tracer() {
        Tracer tracer = mock(Tracer.class);
        Span mockSpan = mock(Span.class);
        TraceContext mockContext = mock(TraceContext.class);

        when(tracer.currentSpan()).thenReturn(mockSpan);
        when(mockSpan.context()).thenReturn(mockContext);
        when(mockContext.traceId()).thenReturn("traceId");
        return tracer;
    }
}
