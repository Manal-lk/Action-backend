package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.OrganizationEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.OrganizationRepository;
import com.xelops.actionplan.utils.ClaimUtility;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {
    @Value("${actionplan-platform.url}")
    private String platformUrl;

    private final OrganizationRepository organizationRepository;
    private final KeycloakRealmService keycloakRealmService;
    private final Messages messages;

    public Organization getOrganizationByRealm(String realm) throws NotFoundException {
        log.info("Start service getOrganizationByRealm | realm: {}", realm);
        Organization organization = organizationRepository.findByRealm(realm)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND_BY_FIELD, ModuleEnum.ORGANIZATION.getName(), "Realm", realm)));
        log.info("End service getOrganizationByRealm | realm: {}", realm);
        return organization;
    }

    public String getUrlPlatform() {
        return platformUrl + "/" + OrganizationEnum.getPathByRealm(ClaimUtility.getConnectedRealm());
    }

    public Organization getCurrentOrganization() throws NotFoundException {
        log.info("Start service getCurrentRealmOrganization");
        final var currentRealm = keycloakRealmService.getRealmForFeignClient();
        Organization organization = getOrganizationByRealm(currentRealm);
        log.info("End service getCurrentRealmOrganization | realm: {}", currentRealm);
        return organization;
    }
}
