package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.KeycloakUserAttributeEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserMapper;
import com.xelops.actionplan.repository.UserRepository;
import com.xelops.actionplan.utils.ClaimUtility;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_ALREADY_EXISTS = "error.ws.userAlreadyExists";

    private final Messages messages;
    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrganizationService organizationService;
    private final UserInvitationService userInvitationService;

    // ===============================
    // GET CONNECTED USER
    // ===============================
    public UserPrivilegesDto getAndCreateConnectedUserIfNotExist() throws FunctionalException, NotFoundException {
        log.info("Start service getAndCreateConnectedUserIfNotExist");

        String keycloakId = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.KEYCLOAK_ID);
        Optional<User> existingUser = userRepository.findByKeycloakId(keycloakId);

        User user = existingUser.orElseGet(() -> {
            try {
                return createConnectedUser(keycloakId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        UserPrivilegesDto dto = userMapper.toUserPrivilegesDto(user);
        log.info("End service getAndCreateConnectedUserIfNotExist");
        return dto;
    }

    private User createConnectedUser(String keycloakId) throws FunctionalException, NotFoundException {
        String username = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.USERNAME);
        String email    = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.EMAIL);
        String fullName = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.FULL_NAME);

        Organization organization = organizationService.getOrganizationByRealm(ClaimUtility.getConnectedRealm());

        validateUserUniqueness(username, email, organization.getId());

        User user = User.builder()
                .username(username)
                .keycloakId(keycloakId)
                .email(email)
                .fullname(fullName)
                .role(resolveUserRole())
                .organization(organization)
                .build();

        return userRepository.save(user);
    }

    private void validateUserUniqueness(String username, String email, Long orgId) throws FunctionalException {
        if (userRepository.existsByUsernameAndOrganizationId(username, orgId)) {
            throw new FunctionalException(messages.get(USER_ALREADY_EXISTS));
        }
        if (email != null && userRepository.existsByEmailAndOrganizationId(email, orgId)) {
            throw new FunctionalException(messages.get(USER_ALREADY_EXISTS));
        }
    }

    private UserRoleEnum resolveUserRole() {
        if (userHelperService.isSuperAdmin()) return UserRoleEnum.SUPER_ADMIN;
        if (userHelperService.isAdmin())      return UserRoleEnum.ADMIN;
        return UserRoleEnum.SIMPLE_USER;
    }

    // ===============================
    // GET ALL USERS
    // ===============================
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDto(users);
    }

    // ===============================
    // FILTER USERS
    // ===============================
    public Page<UserFilterDto> filterUsers(UserFilterCriteriaDto criteria) {

        List<User> users;
        try {
            Long organizationId = userHelperService.getOrganizationIdFromUser();
            users = userRepository.findByOrganizationId(organizationId);
        } catch (Exception e) {
            users = userRepository.findAll();
        }

        List<UserFilterDto> filteredUsers = users.stream()
                .filter(user -> criteria.getSearch() == null || criteria.getSearch().isEmpty() ||
                        user.getUsername().toLowerCase().contains(criteria.getSearch().toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(criteria.getSearch().toLowerCase()))
                .filter(user -> criteria.getRoles() == null || criteria.getRoles().isEmpty() ||
                        criteria.getRoles().contains(user.getRole().name()))
                .map(userMapper::toUserFilterDto)
                .toList();

        return new PageImpl<>(filteredUsers);
    }

    // ===============================
    // SEARCH FOR WORKSPACE OR BOARD  (utilisé par UserDetailsResource)
    // ===============================
    public List<UserDto> searchUsersForWorkspaceOrBoard(String fullName, Long workspaceId, Long boardId)
            throws FunctionalException, NotFoundException {
        log.info("Start service searchUsersForWorkspaceOrBoard | fullName: {}, workspaceId: {}, boardId: {}",
                fullName, workspaceId, boardId);

        Long organizationId = userHelperService.getOrganizationIdFromUser();

        List<User> users;
        if (workspaceId != null) {
            users = userRepository.findByOrganizationAndFullNameNotInWorkspace(organizationId, fullName, workspaceId);
        } else if (boardId != null) {
            users = userRepository.findByOrganizationAndFullNameNotInBoard(organizationId, fullName, boardId);
        } else {
            throw new FunctionalException(messages.get(GlobalConstants.USER_INVITATION_WORKSPACE_OR_BOARD_REQUIRED));
        }

        List<UserDto> result = userMapper.toUserDto(users);
        log.info("End service searchUsersForWorkspaceOrBoard | found {} users", result.size());
        return result;
    }

    // ===============================
    // SEARCH IN WORKSPACE  (utilisé par UserDetailsResource)
    // ===============================
    public List<UserDto> searchUsersInWorkspace(String fullName, Long workspaceId) throws NotFoundException {
        log.info("Start service searchUsersInWorkspace | fullName: {}, workspaceId: {}", fullName, workspaceId);

        Organization organization = organizationService.getOrganizationByRealm(ClaimUtility.getConnectedRealm());

        List<User> users = userRepository.findByOrganizationAndFullNameInWorkspace(
                organization.getId(), fullName, workspaceId);

        List<UserDto> result = userMapper.toUserDto(users);
        log.info("End service searchUsersInWorkspace | found {} users", result.size());
        return result;
    }
}