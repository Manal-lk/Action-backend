package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.*;
import com.xelops.actionplan.dto.UserInvitationDto;
import com.xelops.actionplan.enumeration.ExistsByEnum;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserInvitationMapper;
import com.xelops.actionplan.repository.UserInvitationRepository;
import com.xelops.actionplan.repository.UserRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserInvitationService {

    private final UserInvitationRepository userInvitationRepository;
    private final UserRepository userRepository;
    private final UserHelperService userHelperService;
    private final UserInvitationMapper userInvitationMapper;
    private final UserBoardService userBoardService;
    private final UserWorkspaceService userWorkspaceService;
    private final WorkspaceService workspaceService;
    private final UserInvitationNotificationService userInvitationNotificationService;
    private final BoardHelperService boardHelperService;
    private final Messages messages;

    private UserInvitation getInvitationById(Long invitationId) throws NotFoundException {
        log.info("Start service getInvitationById | by ID: {}", invitationId);

        UserInvitation userInvitation = userInvitationRepository.findById(invitationId)
                .orElseThrow(() ->
                        new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND_BY_FIELD,
                                ModuleEnum.USER_INVITATION.getName(), ExistsByEnum.ID.name(), invitationId))
                );

        log.info("End service getInvitationById | by ID: {}", userInvitation.getId());
        return userInvitation;
    }

    @Transactional
    public void createInvitation(UserInvitationDto userInvitationDto) throws FunctionalException, NotFoundException {
        log.info("Start service createInvitation | emails: {}", userInvitationDto.emails());
        Workspace workspace = null;
        Board board = null;

        if (userInvitationDto.boardId() == null && userInvitationDto.workspaceId() == null) {
            log.error("Either workspaceId or boardId must be provided");
            throw new FunctionalException(messages.get(GlobalConstants.USER_INVITATION_WORKSPACE_OR_BOARD_REQUIRED));
        }
        Long orgaId = null;
        // Validate workspace exists if provided
        if (userInvitationDto.workspaceId() != null) {
            workspace = workspaceService.getWorkspaceById(userInvitationDto.workspaceId());
            // Verify organization access
            orgaId = userHelperService.verifyOrganizationAccess(workspace.getOrganization().getId());
        }
        // Validate board exists if provided
        if (userInvitationDto.boardId() != null) {
            board = boardHelperService.getById(userInvitationDto.boardId());
            // Verify organization access
            orgaId = userHelperService.verifyOrganizationAccess(board.getWorkspace().getOrganization().getId());
        }
        List<User> existingUsers = userRepository.findByEmailInAndOrganizationId(userInvitationDto.emails(), orgaId);
        User connectedUser = userHelperService.getConnectedUser();

        List<UserInvitation> invitations = userInvitationMapper.toUserInvitationList(userInvitationDto, workspace, board, connectedUser);
        List<UserInvitation> invitationForNotExistingUsers = invitations.stream()
                .filter(invitation -> existingUsers.stream()
                        .noneMatch(user -> user.getEmail().equalsIgnoreCase(invitation.getEmail())))
                .toList();
        List<UserInvitation> invitationForExistingUsers = invitations.stream()
                .filter(invitation -> existingUsers.stream()
                        .anyMatch(user -> user.getEmail().equalsIgnoreCase(invitation.getEmail())))
                .toList();

        //Organization Members
        if (!invitationForExistingUsers.isEmpty()) {
            linkUsers(invitationForExistingUsers, existingUsers);
        }
        //Invitations for users are not existing on the organization
        if (!invitationForNotExistingUsers.isEmpty()) {
            userInvitationRepository.saveAll(invitationForNotExistingUsers);
        }
        // send notifications for user invitations for board
        if (userInvitationDto.boardId() != null) {
            userInvitationNotificationService.sendBoardInvitationsNotification(invitations, existingUsers);

        }
        //send notifications for user invitations for workspace
        if (userInvitationDto.workspaceId() != null) {
            log.info("test notification for existing users : {}", existingUsers.size());
            userInvitationNotificationService.sendWorkspaceInvitationNotification(invitations, existingUsers);
        }

        log.info("End service createInvitation | existing users invitations: {} | not existing users invitations: {}",
                invitationForExistingUsers.size(), invitationForNotExistingUsers.size());
    }

    public void linkUsers(List<UserInvitation> invitations, List<User> users) {
        log.info("Start LinkUsers | invitationsCount={}", invitations.size());

        if (invitations.isEmpty()) {
            log.info("End LinkUsers | invitationsCount=0");
            return;
        }
        Map<String, User> usersByEmail = users.stream()
                .collect(Collectors.toMap(
                        user -> user.getEmail().toLowerCase(),
                        Function.identity()
                ));
        //Création des UserWorkspace
        List<UserWorkspace> userWorkspaces = invitations.stream()
                .filter(inv -> inv.getWorkspace() != null)
                .map(inv -> {
                    User user = usersByEmail.get(inv.getEmail().toLowerCase());
                    return buildUserWorkspace(inv, user);
                })
                .filter(Objects::nonNull)
                .toList();

        if (!userWorkspaces.isEmpty()) {
            userWorkspaceService.saveAllUserWorkspaces(userWorkspaces);
        }

        //Invitations directes sur un board
        List<UserBoard> userBoards = invitations.stream()
                .filter(inv -> inv.getBoard() != null)
                .map(inv -> {
                    User user = usersByEmail.get(inv.getEmail().toLowerCase());
                    return buildUserBoard(inv, user);
                })
                .filter(Objects::nonNull)
                .toList();

        if (!userBoards.isEmpty()) {
            userBoardService.saveAllUserBoards(userBoards);
        }

        log.info("End LinkUsers | invitationsCount={}", invitations.size());
    }

    @Transactional
    public void validateAndLinkUserInvitation(String token) throws NotFoundException {
        log.info("Start service validateAndLinkUserInvitation | token: {}", token);
        UserInvitation userInvitation = getInvitationByTokenAndStatus(token, UserInvitationStatusEnum.PENDING);
        UserInvitation acceptedInvitation = updateInvitationStatus(userInvitation, UserInvitationStatusEnum.ACCEPTED);
        if (acceptedInvitation != null) {
            linkUser(acceptedInvitation);
        }
        log.info("End service validateAndLinkUserInvitation | token: {}", token);
    }

    public UserInvitation getInvitationByTokenAndStatus(String token, UserInvitationStatusEnum status) throws NotFoundException {
        log.info("Start service getInvitationByTokenAndStatus | token: {} | status: {}", token, status);
        UserInvitation userInvitation = userInvitationRepository.findByTokenAndStatus(token, status)
                .orElseThrow(() ->
                        new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND_BY_FIELD,
                                ModuleEnum.USER_INVITATION.getName(), "token", token))
                );
        log.info("End service getInvitationByTokenAndStatus | token: {} | status: {} | userInvitation: {}", token, status, userInvitation.getId());
        return userInvitation;
    }

    public UserInvitation updateInvitationStatus(UserInvitation userInvitation, UserInvitationStatusEnum status) {
        log.info("Start service updateInvitationStatus | email: {} | status: {}", userInvitation.getEmail(), status);
        userInvitation.setStatus(status);
        UserInvitation savedInvitation = userInvitationRepository.save(userInvitation);
        log.info("End service updateInvitationStatus | userEmail: {} | newStatus: {}", savedInvitation.getEmail(), status);
        return savedInvitation;
    }


    public void linkUser(UserInvitation invitation) throws NotFoundException {
        log.info("Start service linkUser | user: {} | workspace: {} | board: {} ", invitation.getEmail(), invitation.getWorkspace(), invitation.getBoard());
        Long organizationId = userHelperService.getOrganizationIdFromUser();
        User user = userRepository.findByEmailAndOrganizationId(invitation.getEmail(), organizationId);
        if (invitation.getWorkspace() != null) {
            UserWorkspace userWorkspaces = buildUserWorkspace(invitation, user);
            userWorkspaceService.saveUserWorkspace(userWorkspaces);
        } else {
            UserBoard userBoard = buildUserBoard(invitation, user);
            userBoardService.saveUserBoard(userBoard);
        }
        log.info("End service linkUser | user: {} | workspace: {} | board: {} ", invitation.getEmail(), invitation.getWorkspace(), invitation.getBoard());
    }

    private UserWorkspace buildUserWorkspace(UserInvitation inv, User user) {
        return UserWorkspace.builder()
                .user(user)
                .workspace(inv.getWorkspace())
                .profile(inv.getProfile())
                .createdBy(inv.getCreatedBy())
                .build();
    }

    private UserBoard buildUserBoard(UserInvitation inv, User user) {
        return UserBoard.builder()
                .user(user)
                .board(inv.getBoard())
                .profile(inv.getProfile())
                .createdBy(inv.getCreatedBy())
                .build();
    }

    public void updateUserInvitationProfile(String email, Long boardId, Long workspaceId, UserProfileEnum profile) throws FunctionalException {
        log.info("Start service updateUserInvitationProfile | email: {} | boardId: {} | profile: {}", email, boardId, profile);
        UserInvitation userInvitation = findUserInvitation(email, boardId, workspaceId);
        userInvitation.setProfile(profile);
        userInvitationRepository.save(userInvitation);
        log.info("End service updateUserInvitationProfile | email: {} | boardId: {} | profile: {}", email, boardId, profile);
    }

    public UserInvitation findUserInvitation(String email, Long boardId, Long workspaceId) throws FunctionalException {
        log.info("Start service findUserInvitation | email: {} | boardId: {} | workspaceId: {}", email, boardId, workspaceId);
        Optional<UserInvitation> userInvitation;
        if (workspaceId != null) {
            userInvitation = userInvitationRepository.findByEmailAndStatusAndWorkspaceId(email, UserInvitationStatusEnum.PENDING, workspaceId);
        } else {
            userInvitation = userInvitationRepository.findByEmailAndStatusAndBoardId(email, UserInvitationStatusEnum.PENDING, boardId);
        }
        if (userInvitation.isEmpty()) {
            throw new FunctionalException(
                    messages.get(
                            GlobalConstants.ERROR_WS_NOT_FOUND,
                            ModuleEnum.USER_INVITATION.getName()
                    )
            );
        }
        log.info("End service findUserInvitation | email: {} | boardId: {} | workspaceId: {}", email, boardId, workspaceId);
        return userInvitation.get();
    }
}
