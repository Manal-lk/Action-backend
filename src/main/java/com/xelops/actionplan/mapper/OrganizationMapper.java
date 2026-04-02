package com.xelops.actionplan.mapper;

import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.dto.OrganizationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationDto toDto(Organization organization);
    Organization toEntity(OrganizationDto organizationDto);
}

