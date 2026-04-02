package com.xelops.actionplan.utils;

import com.xelops.actionplan.enumeration.KeycloakUserAttributeEnum;
import lombok.experimental.UtilityClass;

import static com.xelops.actionplan.utils.JWTUtility.getClaim;

@UtilityClass
public class ClaimUtility {

    public static String getConnectedUserFieldByName(KeycloakUserAttributeEnum name) {
        return getClaim(name.getAttribute());
    }

    public static String getConnectedRealm() {
        String issuer = getConnectedUserFieldByName(KeycloakUserAttributeEnum.ISS);
        return issuer.substring(issuer.lastIndexOf("/") + 1);
    }

    public static Long getOrganizationId() {
        String orgId = getConnectedUserFieldByName(KeycloakUserAttributeEnum.ORGANIZATION_ID);
        return orgId != null ? Long.valueOf(orgId) : null;
    }

}
