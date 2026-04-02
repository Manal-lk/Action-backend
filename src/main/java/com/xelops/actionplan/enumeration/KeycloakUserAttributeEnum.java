package com.xelops.actionplan.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeycloakUserAttributeEnum {

    KEYCLOAK_ID("sub"),
    EMAIL("email"),
    FULL_NAME("name"),
    ISS("iss"),
    USERNAME("preferred_username"),
    ORGANIZATION_ID("organizationId");

    private final String attribute;
}
