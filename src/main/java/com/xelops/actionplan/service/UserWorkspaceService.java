package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.domain.UserInvitation;
import com.xelops.actionplan.domain.UserWorkspace;
import com.xelops.actionplan.domain.Workspace;
import com.xelops.actionplan.dto.UserProfileDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserInvitationMapper;
import com.xelops.actionplan.mapper.UserWorkspaceMapper;
import com.xelops.actionplan.repository.UserInvitationRepository;
import com.xelops.actionplan.repository.UserWorkspaceRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserWorkspaceService {

    private final UserWorkspaceRepository userWorkspaceRepository;
    private final Messages messages;
    private final UserInvitationRepository userInvitationRepository;
    private final UserWorkspaceMapper userWorkspaceMapper;
    private final UserInvitationMapper userInvitationMapper;
    private final UserHelperService userHelperService;

    public void saveUserWorkspace(Workspace workspace, User user, UserProfileEnum profile) {
        log.info("Start service saveUserWorkspace | workspaceId: {} | userId: {} | profile: {}", workspace.getName(), user.getId(), profile);

        UserWorkspace userWorkspace = UserWorkspace.builder()
                .workspace(workspace)
                .user(user)
                .profile(profile)
                .createdBy(user)
                .build();
        userWorkspaceRepository.save(userWorkspace);

        log.info("End service saveUserWorkspace | workspaceId: {} | userId: {} | profile: {}", workspace.getName(), user.getId(), profile);

    }

    /**
     * Sauvegarde une liste de UserWorkspace en base de données.
     *
     * @param userWorkspaces la liste des UserWorkspace à sauvegarder
     * @return la liste des UserWorkspace sauvegardés
     */
    public List<UserWorkspace> saveAllUserWorkspaces(List<UserWorkspace> userWorkspaces) {
        log.info("Start service saveAllUserWorkspaces | userWorkspaces count: {}", userWorkspaces != null ? userWorkspaces.size() : 0);
        if (userWorkspaces == null || userWorkspaces.isEmpty()) {
            log.warn("No UserWorkspaces provided to save");
            return List.of();
        }
        List<UserWorkspace> savedUserWorkspaces = userWorkspaceRepository.saveAll(userWorkspaces);
        log.info("End service saveAllUserWorkspaces | saved count: {}", savedUserWorkspaces.size());
        return savedUserWorkspaces;
    }

    public UserWorkspace saveUserWorkspace(UserWorkspace userWorkspace) {
        log.info("Start service saveUserWorkspace | userWorkspace {}", userWorkspace.getWorkspace().getId());
        UserWorkspace savedUserWorkspace = userWorkspaceRepository.save(userWorkspace);
        log.info("End service saveUserWorkspace | savedUserWorkspace id: {}", savedUserWorkspace.getId());
        return savedUserWorkspace;
    }

    public List<UserProfileDto> getWorkspaceUsersAndInvitedUsers(Long workspaceId) throws NotFoundException {
        log.info("Start service getWorkspaceUsersAndInvitedUsers | workspaceId: {}", workspaceId);

        // Verify organization access to workspace (defense in depth)
        List<UserWorkspace> userWorkspaces = userWorkspaceRepository.findAllByWorkspaceId(workspaceId);
        if (!userWorkspaces.isEmpty()) {
            userHelperService.verifyOrganizationAccess(
                    userWorkspaces.get(0).getWorkspace().getOrganization().getId()
            );
        }

        List<UserProfileDto> userProfiles = userWorkspaceMapper.userWorkspaceToUserProfileDto(userWorkspaces);

        // Get all pending invitations for the workspace
        List<UserInvitation> userInvitations = userInvitationRepository.findByWorkspaceIdAndStatus(workspaceId, UserInvitationStatusEnum.PENDING);
        userProfiles.addAll(userInvitationMapper.userInvitationToUserProfileDto(userInvitations));

        log.info("End service getWorkspaceUsersAndInvitedUsers | found {} user profiles", userProfiles.size());
        return userProfiles;
    }

    public void updateUserWorkspaceProfile(Long workspaceId, Long userId, UserProfileEnum profile) throws FunctionalException, NotFoundException {
        log.info("Start service updateUserWorkspaceProfile | workspaceId: {} | userId: {} | profile: {}", workspaceId, userId, profile);
        UserWorkspace userWorkspace = findUserWorkspace(workspaceId, userId);
        // Verify organization access
        userHelperService.verifyOrganizationAccess(userWorkspace.getWorkspace().getOrganization().getId());
        userWorkspace.setProfile(profile);
        userWorkspaceRepository.save(userWorkspace);
        log.info("End service updateUserWorkspaceProfile | workspaceId: {} | userId: {} | profile: {}", workspaceId, userId, profile);
    }

    public UserWorkspace findUserWorkspace(Long workspaceId, Long userId) throws FunctionalException, NotFoundException {
        log.info("Start service findUserWorkspace | workspaceId: {} | userId: {}", workspaceId, userId);
        UserWorkspace userWorkspace = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_NOT_FOUND,
                                ModuleEnum.USER_WORKSPACE.getName()
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(userWorkspace.getWorkspace().getOrganization().getId());
        log.info("End service saveUserWorkspace | workspaceId: {} | userId: {} | userWorkspace: {}", workspaceId, userId, userWorkspace.getId());
        return userWorkspace;
    }
}
