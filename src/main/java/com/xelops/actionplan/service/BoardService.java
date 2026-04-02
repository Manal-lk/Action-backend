package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.*;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.BoardColumnMapper;
import com.xelops.actionplan.mapper.BoardMapper;
import com.xelops.actionplan.mapper.UserMapper;
import com.xelops.actionplan.repository.BoardColumnRepository;
import com.xelops.actionplan.repository.BoardRepository;
import com.xelops.actionplan.repository.UserBoardRepository;
import com.xelops.actionplan.repository.UserWorkspaceRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserWorkspaceRepository userWorkspaceRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UserBoardRepository userBoardRepository;
    private final UserHelperService userHelperService;
    private final BoardColumnMapper boardColumnMapper;
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final Messages messages;
    private final UserMapper userMapper;
    private final BoardColumnService boardColumnService;
    private final ActionService actionService;
    private final BoardHelperService boardHelperService;

    private void checkUserInWorkspace(Long userId, Long workspaceId) throws FunctionalException {
        userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new FunctionalException(messages.get(GlobalConstants.USER_NOT_IN_WORKSPACE_ERROR, userId, workspaceId)));
    }


    public Long create(BoardCreationUpdateDto boardCreation, MultipartFile image) throws FunctionalException, NotFoundException {
        log.info("Start service create Board");
        final var userId = userHelperService.getConnectedUserDetails().userId();
        final var workspaceId = boardCreation.workspaceId();
        checkUserInWorkspace(userId, workspaceId);
        final var duplicateBoard = boardRepository.findByNameAndWorkspace_Id(boardCreation.name(), workspaceId);
        if (duplicateBoard.isPresent()) {
            throw new FunctionalException(messages.get(GlobalConstants.BOARD_DUPLICATE_NAME_ERROR, boardCreation.name(), workspaceId));
        }
        final var board = boardMapper.toBoard(boardCreation, userId);
        // Verify organization access for the workspace
        Workspace workspace = board.getWorkspace();
        if (workspace != null && workspace.getOrganization() != null) {
            userHelperService.verifyOrganizationAccess(workspace.getOrganization().getId());
        }
        final var savedBoard = boardRepository.save(board);
        if (image != null && !image.isEmpty()) {
            // TODO: Handle image upload logic here
        }
        userBoardRepository.save(
                UserBoard.builder()
                        .board(savedBoard)
                        .user(User.builder().id(userId).build())
                        .profile(UserProfileEnum.ADMINISTRATOR)
                        .build()
        );
/*
        try {
            if (!users.isEmpty()) {
                final var notificationsData = users.stream()
                        .map(user -> NotificationDataDto.builder()
                                .type(NotificationTypeEnum.APP)
                                .recipientEmail(user.getEmail())
                                .recipientId(user.getKeycloakId())
                                .titleData(Map.of())
                                .bodyData(Map.of("boardName", savedBoard.getName()))
                                .build()
                        )
                        .toList();
                final var emailNotificationsData = users.stream()
                        .map(user -> NotificationDataDto.builder()
                                .type(NotificationTypeEnum.EMAIL)
                                .recipientEmail(user.getEmail())
                                .recipientId(user.getKeycloakId())
                                .titleData(Map.of("boardName", savedBoard.getName()))
                                .bodyData(Map.of(
                                        "boardName", savedBoard.getName(),
                                        GlobalConstants.NOTIFICATION_APP_LINK_KEY, platformUrl + "/boards/" + boardId.toString()
                                ))
                                .build()
                        )
                        .toList();
                final var newNotification = NewNotificationsDto.builder()
                        .platformId(PlatformEnum.ACTIONS.name())
                        .template(NotificationTemplateEnum.NEW_BOARD_CREATED_NOTIF)
                        .notificationsData(notificationsData)
                        .build();
                final var newEmailNotification = NewNotificationsDto.builder()
                        .platformId(PlatformEnum.ACTIONS.name())
                        .template(NotificationTemplateEnum.NEW_BOARD_CREATED)
                        .notificationsData(emailNotificationsData)
                        .build();

                // TODO: to be optimized
                notificationClient.create(newNotification);
                notificationClient.create(newEmailNotification);
                notificationsData.stream()
                        .map(NotificationDataDto::recipientId)
                        .forEach(notificationService::notifyUser);
            }
        } catch (Exception e) {
            log.error("Error Service : Sending notification", e);
        }*/
        log.info("End service create Board | boardId: {}", savedBoard.getId());
        return savedBoard.getId();
    }

    @Transactional
    public void update(Long boardId, BoardCreationUpdateDto boardUpdate, MultipartFile image) throws FunctionalException, NotFoundException {
        log.info("Start service update Board | boardId: {}", boardId);
        final var userId = userHelperService.getConnectedUserDetails().userId();
        final var workspaceId = boardUpdate.workspaceId();
        checkUserInWorkspace(userId, workspaceId);
        final var duplicateBoard = boardRepository.findByNameAndWorkspace_Id(boardUpdate.name(), workspaceId);
        if (duplicateBoard.isPresent() && !duplicateBoard.get().getId().equals(boardId)) {
            throw new FunctionalException(messages.get(GlobalConstants.BOARD_DUPLICATE_NAME_ERROR, boardUpdate.name(), workspaceId));
        }
        final var existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_NOT_FOUND,
                                ModuleEnum.BOARD.getName(),
                                boardId
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(existingBoard.getWorkspace().getOrganization().getId());
        if (image != null && !image.isEmpty()) {
            // TODO: Handle image upload logic here
        }
        final var board = boardMapper.toBoard(boardUpdate, userId);
        boardMapper.toUpdatedBoard(existingBoard, board);
        final var updated = boardRepository.save(existingBoard);
        log.info("End service update Board | boardId: {}", updated.getId());
    }

    public void createColumn(Long boardId, BoardColumnCreationUpdateDto boardColumn) throws FunctionalException, NotFoundException {
        log.info("Start service create Board Column | boardId: {}", boardId);
        final var existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_NOT_FOUND,
                                ModuleEnum.BOARD.getName(),
                                boardId
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(existingBoard.getWorkspace().getOrganization().getId());
        if (existingBoard.getColumns().size() == 10) {
            throw new FunctionalException(messages.get(GlobalConstants.BOARD_MAX_COLUMNS_ERROR, boardId, 10));
        }
        final var boardColumnEntity = boardColumnMapper.toBoardColumn(boardColumn);
        boardColumnRepository.save(boardColumnEntity);
        log.info("End service create Board Column | boardColumnId: {}, boardId: {}", boardColumnEntity.getId(), boardId);
    }

    public void updateColumn(Long boardId, Long boardColumnId, BoardColumnCreationUpdateDto boardColumnUpdate) throws FunctionalException, NotFoundException {
        log.info("Start service update Board Column | boardColumnId: {}, boardId: {}", boardColumnId, boardId);
        final var existingBoardColumn = boardColumnRepository.findById(boardColumnId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_COLUMN_NOT_FOUND,
                                ModuleEnum.BOARD.getName(),
                                boardColumnId
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(existingBoardColumn.getBoard().getWorkspace().getOrganization().getId());
        final var boardColumnEntity = boardColumnMapper.toBoardColumn(boardColumnUpdate);
        boardColumnMapper.toUpdatedBoardColumn(existingBoardColumn, boardColumnEntity);
        final var updated = boardColumnRepository.save(existingBoardColumn);
        log.info("End service update Board Column | boardColumnId: {}, boardId: {}", updated.getId(), boardId);
    }

    public void deleteColumn(Long boardId, Long boardColumnId) throws FunctionalException, NotFoundException {
        log.info("Start service delete Board Column | boardColumnId: {}, boardId: {}", boardColumnId, boardId);
        final var existingBoardColumn = boardColumnRepository.findById(boardColumnId)
                .orElseThrow(() -> new FunctionalException(
                        messages.get(
                                GlobalConstants.ERROR_WS_COLUMN_NOT_FOUND,
                                ModuleEnum.BOARD.getName(),
                                boardColumnId
                        )
                ));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(existingBoardColumn.getBoard().getWorkspace().getOrganization().getId());
        if (!existingBoardColumn.getActions().isEmpty()) {
            throw new FunctionalException(
                    messages.get(
                            GlobalConstants.ERROR_WS_COLUMN_NOT_EMPTY,
                            ModuleEnum.BOARD.getName(),
                            boardColumnId
                    )
            );
        }
        boardColumnRepository.delete(existingBoardColumn);
        log.info("End service delete Board Column | boardColumnId: {}, boardId: {}", boardColumnId, boardId);
    }

    public Page<BoardDto> getUserBoards(Long workspaceId, Integer page, Integer size) throws NotFoundException {
        log.info("Start service getUserBoards | page: {}, size: {}", page, size);
        final var userPrivileges = userHelperService.getConnectedUserDetails();
        final var userId = userPrivileges.userId();
        final var pageable = PageRequest.of(
                page,
                size,
                Sort.by("updatedAt").descending().and(Sort.by("name").ascending()));
        final Page<Board> userBoards;
        if (userPrivileges.role() == UserRoleEnum.ADMIN) {
            final var organizationId = userPrivileges.organization().id();
            userBoards = workspaceId != null ?
                    boardRepository.findByWorkspace_Organization_IdAndWorkspace_Id(organizationId, workspaceId, pageable) :
                    boardRepository.findByWorkspace_Organization_Id(organizationId, pageable);
        } else {
            userBoards = workspaceId != null ?
                    boardRepository.findByUserBoards_User_IdAndWorkspace_Id(userId, workspaceId, pageable) :
                    boardRepository.findByUserBoards_User_Id(userId, pageable);
        }
        final var boardDtos = userBoards.map(boardMapper::toBoardDto);
        log.info("End service getUserBoards | page: {}, size: {}", page, size);
        return boardDtos;
    }

    public Page<UserSimplifiedDto> getBoardUsers(Long boardId, Integer page, Integer size) throws FunctionalException, NotFoundException {
        log.info("Start service getBoardUsers | page: {}, size: {}", page, size);
        if (!userHelperService.hasAccessToBoard(boardId)) {
            throw new FunctionalException(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR, boardId));
        }

        // Verify organization access (defense in depth)
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.BOARD_NOT_FOUND_ERROR, boardId)));
        userHelperService.verifyOrganizationAccess(board.getWorkspace().getOrganization().getId());

        Pageable pageable = PageRequest.of(page, size);
        Page<User> boardUsers = boardRepository.getBoardUsers(boardId, pageable);
        Page<UserSimplifiedDto> userSimplifiedPage = boardUsers.map(userMapper::toUserSimplified);
        log.info("End service getBoardUsers | page: {}, size: {}, result size: {}", page, size, userSimplifiedPage.getContent().size());
        return userSimplifiedPage;
    }

    public BoardSimplifiedDto getSimplifiedBoardById(Long boardId) throws NotFoundException {
        log.info("Start service getSimplifiedBoardById | id: {}", boardId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND, ModuleEnum.BOARD.getName(), boardId)));
        // Verify organization access
        userHelperService.verifyOrganizationAccess(board.getWorkspace().getOrganization().getId());
        BoardSimplifiedDto boardSimplifiedDto = boardMapper.toBoardSimplifiedDto(board);
        log.info("End service getSimplifiedBoardById | id: {}", boardId);
        return boardSimplifiedDto;
    }

    public void checkDuplicateBoardName(String name, Long workspaceId) throws FunctionalException {
        Optional<Board> existingBoard = boardRepository.findByNameAndWorkspace_Id(name, workspaceId);
        if (existingBoard.isPresent()) {
            throw new FunctionalException(
                    messages.get(GlobalConstants.BOARD_DUPLICATE_NAME_ERROR, name, workspaceId)
            );
        }
    }

    public List<BoardColumnDto> getBoardColumnsByBoardId(Long boardId, ActionFilterDto actionFilter) throws NotFoundException {
        log.info("Start service getBoardColumnsByBoardId | boardId: {} | actionFilter: {}", boardId, actionFilter);
        Board board = boardHelperService.getById(boardId);
        userHelperService.verifyOrganizationAccess(board.getWorkspace().getOrganization().getId());
        List<BoardColumn> boardColumns = boardColumnService.findAllByBoardIdOrderByOffset(boardId);
        if (actionFilter != null) {

            Map<Long, List<Action>> actionsByColumnId = actionService
                    .findAllByBoardIdAndFilterOptions(boardId, actionFilter)
                    .stream()
                    .collect(Collectors.groupingBy(a -> a.getBoardColumn().getId()));

            boardColumns = boardColumns.stream()
                    .map(col -> BoardColumn
                            .builder()
                            .id(col.getId())
                            .name(col.getName())
                            .offset(col.getOffset())
                            .actions(actionsByColumnId.getOrDefault(col.getId(), Collections.emptyList()))
                            .build()
                    ).collect(Collectors.toList());
        }
        List<BoardColumnDto> boardColumnDtos = boardColumnMapper.toBoardColumnDtos(boardColumns);
        log.info("End service getBoardColumnsByBoardId | boardId: {} | size: {}", boardId, boardColumnDtos.size());
        return boardColumnDtos;
    }
}
