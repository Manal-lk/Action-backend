package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.enumeration.KeycloakUserAttributeEnum;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserMapper;
import com.xelops.actionplan.repository.UserRepository;
import com.xelops.actionplan.repository.UserWorkspaceRepository;
import com.xelops.actionplan.utils.ClaimUtility;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xelops.actionplan.utils.constants.GlobalConstants.ROLE_PREFIX;

@Slf4j
@Service
@AllArgsConstructor
public class UserHelperService {

    private final UserRepository userRepository;
    private final Messages messages;
    private final UserMapper userMapper;
    private final UserWorkspaceRepository userWorkspaceRepository;


    public boolean hasRole(UserRoleEnum roleEnum) {
        log.info("Start Service hasRole | role: {}", roleEnum);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            log.info("End service hasRole | Cannot extract claims from null authentication");
            return false;
        }
        boolean hasRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> Objects.equals(grantedAuthority.getAuthority(), ROLE_PREFIX + roleEnum.name()));
        log.info("End Service hasRole | role: {} | result: {}", roleEnum, hasRole);
        return hasRole;
    }

    public boolean isSuperAdmin() {
        log.info("Start Service isSuperAdmin");
        boolean hasSuperAdminRole = hasRole(UserRoleEnum.SUPER_ADMIN);
        log.info("End Service isSuperAdmin: {}", hasSuperAdminRole);
        return hasSuperAdminRole;
    }

    public boolean isAdmin() {
        log.info("Start Service isAdmin");
        boolean hasAdminRole = hasRole(UserRoleEnum.ADMIN);
        log.info("End Service isAdmin: {}", hasAdminRole);
        return hasAdminRole;
    }

    public UserPrivilegesDto getConnectedUserDetails() throws NotFoundException {
        log.info("Start Service getConnectedUserDetails");
        User user = getConnectedUser();
        UserPrivilegesDto userPrivilegesDto = userMapper.toUserPrivilegesDto(user);
        log.info("End Service getConnectedUserDetails | result: {}", userPrivilegesDto);
        return userPrivilegesDto;
    }

    public User getConnectedUser() throws NotFoundException {
        log.info("Start service getConnectedUser");
        User user = findUserByKeycloakId(ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.KEYCLOAK_ID));
        log.info("End service getConnectedUser | userId: {}", user.getId());
        return user;
    }

    public User findUserByKeycloakId(String keycloakId) throws NotFoundException {
        log.info("Start service findUserByKeycloakId | keycloakId: {}", keycloakId);
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND_BY_FIELD, ModuleEnum.USER.getName(), "KEYCLOAK_ID", keycloakId)));
        if (user.getOrganization() == null) {
            throw new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_AFFECTED, ModuleEnum.ORGANIZATION.getName()));
        }
        log.info("End service findUserByKeycloakId | keycloakId: {}", keycloakId);
        return user;
    }

    public boolean hasAccessToBoard(Long userId, Long boardId) {
        log.info("Start service hasAccessToBoard | userId: {} | boardId: {}", userId, boardId);
        boolean hasAccess = userRepository.hasAccess(boardId, userId);
        log.info("End service hasAccessToBoard | userId: {} | boardId: {} | hasAccess: {}", userId, boardId, hasAccess);
        return hasAccess;
    }

    public boolean hasAccessToBoard(Long boardId) throws NotFoundException {
        log.info("Start service hasAccessToBoard | boardId: {}", boardId);
        Long connectedUserId = getConnectedUserDetails().userId();
        boolean hasAccess = isAdmin() || hasAccessToBoard(connectedUserId, boardId);
        log.info("End service hasAccessToBoard | boardId: {} | hasAccess: {}", boardId, hasAccess);
        return hasAccess;
    }

    public User findUserById(Long id) {
        log.info("Start service findUserById | id: {}", id);
        User user = userRepository.findById(id)
                .orElse(null);
        log.info("End service findUserById | id: {} | found: {}", id, user != null);
        return user;
    }

    public Map<Long, User> findUsersByIds(List<Long> ids) {
        log.info("Start service findUsersByIds | ids: {}", ids.size());
        List<User> users = userRepository.findAllByIdIn(ids);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        log.info("End service findUsersByIds | ids: {} | users: {}", ids.size(), users.size());
        return userMap;
    }

    public void validateUserInWorkspace(Long userId, Long workspaceId) throws FunctionalException {
        if (userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId).isEmpty()) {
            throw new FunctionalException(
                    messages.get(GlobalConstants.USER_NOT_IN_WORKSPACE_ERROR, userId, workspaceId)
            );
        }
    }

    public Long getOrganizationIdFromUser() throws NotFoundException {
        log.info("Start service getOrganizationIdFromUser");
        UserPrivilegesDto userPrivileges = getConnectedUserDetails();
        Long organizationId = userPrivileges.organization().id();
        log.info("End service getOrganizationIdFromUser | organizationId: {}", organizationId);
        return organizationId;
    }

    public Long verifyOrganizationAccess(Long organizationId) throws NotFoundException {
        log.info("Start service verifyOrganizationAccess | organizationId: {}", organizationId);
        Long userOrgId = getOrganizationIdFromUser();
        if (userOrgId == null || !userOrgId.equals(organizationId)) {
            throw new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND, ModuleEnum.ORGANIZATION.getName(), organizationId));
        }
        log.info("End service verifyOrganizationAccess | organizationId: {}", organizationId);
        return userOrgId;
    }
}
