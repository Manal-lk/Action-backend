package com.xelops.actionplan.dto;

import com.xelops.actionplan.enumeration.UserRoleEnum;
import lombok.Builder;


@Builder
public record UserPrivilegesDto(Long userId,
                                String keycloakId,
                                String username,
                                String fullname,
                                String email,
                                UserRoleEnum role,
                                OrganizationDto organization) {
}
