package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.UserDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
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

    public UserPrivilegesDto getAndCreateConnectedUserIfNotExist() throws FunctionalException, NotFoundException {
        log.info("Start service getAndCreateConnectedUserIfNotExist");
        String keycloakId = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.KEYCLOAK_ID);

        Optional<User> existingUser = userRepository.findByKeycloakId(keycloakId);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = createConnectedUser(keycloakId);
        }
        UserPrivilegesDto userPrivilegesDto = userMapper.toUserPrivilegesDto(user);
        log.info("End service getAndCreateConnectedUserIfNotExist | userId: {}", userPrivilegesDto.userId());
        return userPrivilegesDto;
    }

    private User createConnectedUser(String keycloakId) throws FunctionalException, NotFoundException {
        log.info("Start service createConnectedUser");
        String username = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.USERNAME);
        String email = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.EMAIL);
        String fullName = ClaimUtility.getConnectedUserFieldByName(KeycloakUserAttributeEnum.FULL_NAME);

        Organization organization = organizationService.getOrganizationByRealm(ClaimUtility.getConnectedRealm());

        validateUserUniqueness(username, email, organization.getId());


        User user = User
                .builder()
                .username(username)
                .keycloakId(keycloakId)
                .email(email)
                .fullname(fullName)
                .role(resolveUserRole())
                .organization(organization)
                .build();
        User savedUser = userRepository.save(user);
        log.info("End service createConnectedUser");
        return savedUser;
    }

    private void validateUserUniqueness(String username, String email, Long organizationId) throws FunctionalException {
        if (userRepository.existsByUsernameAndOrganizationId(username, organizationId)) {
            throw new FunctionalException(messages.get(USER_ALREADY_EXISTS, "username", username));
        }
        if (email != null && userRepository.existsByEmailAndOrganizationId(email, organizationId)) {
            throw new FunctionalException(messages.get(USER_ALREADY_EXISTS, "email", email));
        }
    }

    private UserRoleEnum resolveUserRole() {
        if (userHelperService.isSuperAdmin()) {
            return UserRoleEnum.SUPER_ADMIN;
        }
        if (userHelperService.isAdmin()) {
            return UserRoleEnum.ADMIN;
        }
        return UserRoleEnum.SIMPLE_USER;
    }

    public List<UserDto> searchUsersForWorkspaceOrBoard(String fullName, Long workspaceId, Long boardId) throws FunctionalException, NotFoundException {
        log.info("Start service searchUsersForWorkspaceOrBoard | fullName: {}, workspaceId: {}, boardId: {}", fullName, workspaceId, boardId);

        // Get the organization ID from the user privileges to ensure isolation
        Long organizationId = userHelperService.getOrganizationIdFromUser();

        List<User> users;
        if (workspaceId != null) {
            // Search for users not in the workspace
            users = userRepository.findByOrganizationAndFullNameNotInWorkspace(
                    organizationId,
                    fullName,
                    workspaceId
            );
        } else if (boardId != null) {
            // Search for users not in the board
            users = userRepository.findByOrganizationAndFullNameNotInBoard(
                    organizationId,
                    fullName,
                    boardId
            );
        } else {
            log.error("Either workspaceId or boardId must be provided");
            throw new FunctionalException(messages.get(GlobalConstants.USER_INVITATION_WORKSPACE_OR_BOARD_REQUIRED));
        }
        List<UserDto> userDtoList = userMapper.toUserDto(users);

        log.info("End service searchUsersForWorkspaceOrBoard | found {} users", userDtoList.size());
        return userDtoList;
    }

    public List<UserDto> searchUsersInWorkspace(String fullName, Long workspaceId) throws NotFoundException {
        log.info("Start service searchUsersInWorkspace | fullName: {}, workspaceId: {}", fullName, workspaceId);

        Organization organization = organizationService.getOrganizationByRealm(ClaimUtility.getConnectedRealm());
        List<User> users;
        users = userRepository.findByOrganizationAndFullNameInWorkspace(
                organization.getId(),
                fullName,
                workspaceId
        );
        List<UserDto> userDtoList = userMapper.toUserDto(users);

        log.info("End service searchUsersForWorkspaceOrBoard | found {} users", userDtoList.size());
        return userDtoList;
    }
}

