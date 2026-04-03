package com.xelops.actionplan.service;

import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserFilterCriteriaDto;
import com.xelops.actionplan.dto.UserManagementDto;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.UserRepository;
import com.xelops.actionplan.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;

    /**
     * Returns all users of the connected user's organisation,
     * optionally filtered by search (fullname/email) and/or roles.
     */
    public List<UserManagementDto> filterUsers(UserFilterCriteriaDto criteria) throws NotFoundException {
        Long organizationId = userHelperService.getOrganizationIdFromUser();

        log.info("Start service filterUsers | orgId={} criteria={}", organizationId, criteria);

        Specification<User> spec = UserSpecification.withFilters(criteria, organizationId);
        List<User> users = userRepository.findAll(spec);

        List<UserManagementDto> result = users.stream()
                .map(u -> UserManagementDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .fullname(u.getFullname())
                        .role(u.getRole())
                        .build())
                .toList();

        log.info("End service filterUsers | found {} users", result.size());
        return result;
    }
}