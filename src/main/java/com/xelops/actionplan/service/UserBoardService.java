package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.UserBoard;
import com.xelops.actionplan.domain.UserInvitation;
import com.xelops.actionplan.dto.UserProfileDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserBoardMapper;
import com.xelops.actionplan.mapper.UserInvitationMapper;
import com.xelops.actionplan.mapper.UserBoardMapper;
import com.xelops.actionplan.mapper.UserInvitationMapper;
import com.xelops.actionplan.repository.UserBoardRepository;
import com.xelops.actionplan.repository.UserInvitationRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserBoardService {

    private final UserBoardRepository userBoardRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final UserInvitationMapper userInvitationMapper;
    private final UserBoardMapper userBoardMapper;
    private final Messages messages;
    private final UserHelperService userHelperService;

    /**
     * Sauvegarde une liste de UserBoard en base de données.
     *
     * @param userBoards la liste des UserBoard à sauvegarder
     * @return la liste des UserBoard sauvegardés
     */
    public List<UserBoard> saveAllUserBoards(List<UserBoard> userBoards) {
        log.info("Start service saveAllUserBoards | userBoards count: {}", userBoards != null ? userBoards.size() : 0);
        if (userBoards == null || userBoards.isEmpty()) {
            log.warn("No UserBoards provided to save");
            return List.of();
        }
        List<UserBoard> savedUserBoards = userBoardRepository.saveAll(userBoards);
        log.info("End service saveAllUserBoards | saved count: {}", savedUserBoards.size());
        return savedUserBoards;
    }

    public UserBoard saveUserBoard(UserBoard userBoard) {
        log.info("Start service saveUserBoard | userBoard: {}", userBoard);
        UserBoard savedUserBoard = userBoardRepository.save(userBoard);
        log.info("End service saveUserBoard | savedUserBoard: {}", savedUserBoard.getId());
        return savedUserBoard;
    }

    /**
     * Vérifie si un UserBoard existe pour un utilisateur et un board donnés.
     */
    public boolean existsByUserIdAndBoardId(Long userId, Long boardId) {
        return userBoardRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    public List<UserProfileDto> getBoardUsersAndInvitedUsers(Long boardId) throws NotFoundException {
        log.info("Start service getBoardUsersAndInvitedUsers | boardId: {}", boardId);

        // Verify organization access to board (defense in depth)
        // Note: This requires injecting BoardService or BoardRepository
        // For now, verify through first UserBoard if exists
        List<UserBoard> userBoards = userBoardRepository.findAllByBoardId(boardId);
        if (!userBoards.isEmpty()) {
            userHelperService.verifyOrganizationAccess(
                userBoards.get(0).getBoard().getWorkspace().getOrganization().getId()
            );
        }

        List<UserProfileDto> userProfiles = userBoardMapper.userBoardToUserProfileDto(userBoards);

        // Get all pending invitations for the board
        List<UserInvitation> userInvitations = userInvitationRepository.findByBoardIdAndStatus(boardId, UserInvitationStatusEnum.PENDING);
        userProfiles.addAll(userInvitationMapper.userInvitationToUserProfileDto(userInvitations));

        log.info("End service getBoardUsersAndInvitedUsers | found {} user profiles", userProfiles.size());
        return userProfiles;
    }


    public void updateUserBoardProfile(Long boardId, Long userId, UserProfileEnum profile) throws FunctionalException, NotFoundException {
        log.info("Start service updateUserBoardProfile | boardId: {} | userId: {} | profile: {}", boardId, userId, profile);
        UserBoard userBoard = findUserBoard(boardId, userId);
        // Verify organization access
        userHelperService.verifyOrganizationAccess(userBoard.getBoard().getWorkspace().getOrganization().getId());
        userBoard.setProfile(profile);
        userBoardRepository.save(userBoard);
        log.info("End service updateUserBoardProfile | boardId: {} | userId: {} | profile: {}", boardId, userId, profile);
    }

    public UserBoard findUserBoard(Long boardId, Long userId) throws FunctionalException, NotFoundException {
        log.info("Start service findUserBoard | boardId: {} | userId: {}", boardId, userId);
        UserBoard userBoard = userBoardRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_NOT_FOUND,
                                ModuleEnum.USER_BOARD.getName()
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(userBoard.getBoard().getWorkspace().getOrganization().getId());
        log.info("End service findUserBoard | boardId: {} | userId: {} | userBoard: {}", boardId, userId, userBoard.getId());
        return userBoard;
    }
}
