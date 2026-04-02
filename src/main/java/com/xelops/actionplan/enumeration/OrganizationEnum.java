package com.xelops.actionplan.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrganizationEnum {
    PUBLIC("action-plan-public", "public"),
    XELOPS("action-plan-xelops", "xelops");

    private final String realm;
    private final String path;

    public static String getPathByRealm(String realm) {
        for (OrganizationEnum organization : OrganizationEnum.values()) {
            if (organization.getRealm().equals(realm)) {
                return organization.getPath();
            }
        }
        return null;
    }
}

