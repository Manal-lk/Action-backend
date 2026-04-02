package com.xelops.actionplan.resource;

import com.xelops.actionplan.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/organizations")
public class OrganizationResource {

    private final OrganizationService organizationService;

}

