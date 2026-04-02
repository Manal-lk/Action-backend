package com.xelops.actionplan.enumeration;


import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum UserRoleEnum {
    @FieldNameConstants.Include SUPER_ADMIN,
    @FieldNameConstants.Include ADMIN,
    @FieldNameConstants.Include SIMPLE_USER
}