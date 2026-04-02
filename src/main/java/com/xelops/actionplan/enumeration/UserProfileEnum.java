package com.xelops.actionplan.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserProfileEnum {
    MEMBER("Member"),
    ADMINISTRATOR("Administrator");

    private final String label;
}
