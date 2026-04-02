package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.*;
import com.xelops.actionplan.dto.BoardColumnCreationUpdateDto;
import com.xelops.actionplan.dto.BoardCreationUpdateDto;
import com.xelops.actionplan.dto.OrganizationDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.BoardColumnMapper;
import com.xelops.actionplan.mapper.BoardMapper;
import com.xelops.actionplan.repository.BoardColumnRepository;
import com.xelops.actionplan.repository.BoardRepository;
import com.xelops.actionplan.repository.UserBoardRepository;
import com.xelops.actionplan.repository.UserWorkspaceRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    private UserWorkspaceRepository userWorkspaceRepository;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserBoardRepository userBoardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private BoardColumnMapper boardColumnMapper;

    @Mock
    private UserHelperService userHelperService;

    @Mock
    private Messages messages;

    @InjectMocks
    private BoardService boardService;

    @Test
    void shouldCreateBoardSuccessfully() throws FunctionalException, NotFoundException {
        // Arrange
        final var boardCreation = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var userId = 1L;
        final var workspaceId = 2L;
        final var organizationId = 10L;
        final var boardName = "Test Board";
        final var connectedUser = mock(UserPrivilegesDto.class);
        final var organization = mock(Organization.class);
        final var workspace = mock(Workspace.class);
        final var board = mock(Board.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardCreation.workspaceId()).thenReturn(workspaceId);
        when(boardCreation.name()).thenReturn(boardName);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.of(mock(UserWorkspace.class)));
        when(boardRepository.findByNameAndWorkspace_Id(boardName, workspaceId))
                .thenReturn(Optional.empty());
        when(boardMapper.toBoard(boardCreation, userId)).thenReturn(board);
        when(board.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardRepository.save(any())).thenReturn(board);
        when(image.isEmpty()).thenReturn(false);

        // Act
        boardService.create(boardCreation, image);

        // Assert
        verify(userWorkspaceRepository).findByUserIdAndWorkspaceId(userId, workspaceId);
        verify(boardRepository).findByNameAndWorkspace_Id(boardName, workspaceId);
        verify(boardMapper).toBoard(boardCreation, userId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
        verify(boardRepository).save(any());
        verify(userBoardRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInWorkspace() throws NotFoundException {
        // Arrange
        final var boardCreation = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var userId = 1L;
        final var workspaceId = 2L;
        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardCreation.workspaceId()).thenReturn(workspaceId);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.empty());
        when(messages.get(GlobalConstants.USER_NOT_IN_WORKSPACE_ERROR, userId, workspaceId))
                .thenReturn("User not in workspace");

        // Act & Assert
        final var exception = assertThrows(FunctionalException.class, () -> boardService.create(boardCreation, image));
        assertEquals("User not in workspace", exception.getMessage());
        verify(userWorkspaceRepository).findByUserIdAndWorkspaceId(userId, workspaceId);
    }

    @Test
    void shouldThrowExceptionWhenBoardNameIsDuplicate() throws NotFoundException {
        // Arrange
        final var boardCreation = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var userId = 1L;
        final var workspaceId = 2L;
        final var boardName = "Duplicate Board";
        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardCreation.workspaceId()).thenReturn(workspaceId);
        when(boardCreation.name()).thenReturn(boardName);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.of(mock(UserWorkspace.class)));
        when(boardRepository.findByNameAndWorkspace_Id(boardName, workspaceId))
                .thenReturn(Optional.of(mock(Board.class)));
        when(messages.get(GlobalConstants.BOARD_DUPLICATE_NAME_ERROR, boardName, workspaceId))
                .thenReturn("Board name is duplicate");

        // Act & Assert
        final var exception = assertThrows(FunctionalException.class, () -> boardService.create(boardCreation, image));
        assertEquals("Board name is duplicate", exception.getMessage());
        verify(boardRepository).findByNameAndWorkspace_Id(boardName, workspaceId);
    }

    @Test
    void shouldUpdateBoardSuccessfully() throws FunctionalException, NotFoundException {
        // Arrange
        final var boardId = 1L;
        final var boardUpdate = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var userId = 1L;
        final var workspaceId = 2L;
        final var organizationId = 10L;
        final var boardName = "Updated Board";
        final var connectedUser = mock(UserPrivilegesDto.class);
        final var organization = mock(Organization.class);
        final var workspace = mock(Workspace.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardUpdate.workspaceId()).thenReturn(workspaceId);
        when(boardUpdate.name()).thenReturn(boardName);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.of(mock(UserWorkspace.class)));
        when(boardRepository.findByNameAndWorkspace_Id(boardName, workspaceId))
                .thenReturn(Optional.empty());
        final var existingBoard = mock(Board.class);
        when(existingBoard.getId()).thenReturn(boardId);
        when(existingBoard.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardRepository.save(any())).thenReturn(existingBoard);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(image.isEmpty()).thenReturn(false);

        // Act
        boardService.update(boardId, boardUpdate, image);

        // Assert
        verify(userWorkspaceRepository).findByUserIdAndWorkspaceId(userId, workspaceId);
        verify(boardRepository).findByNameAndWorkspace_Id(boardName, workspaceId);
        verify(boardRepository).findById(boardId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
        verify(boardMapper).toBoard(boardUpdate, userId);
        verify(boardMapper).toUpdatedBoard(eq(existingBoard), any());
        verify(boardRepository).save(existingBoard);
    }

    @Test
    void shouldThrowExceptionWhenBoardToUpdateNotFound() throws NotFoundException {
        // Arrange
        final var userId = 1L;
        final var workspaceId = 2L;
        final var boardId = 1L;
        final var boardUpdate = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.of(mock(UserWorkspace.class)));
        when(boardUpdate.workspaceId()).thenReturn(workspaceId);
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        when(messages.get(GlobalConstants.ERROR_WS_NOT_FOUND, ModuleEnum.BOARD.getName(), boardId))
                .thenReturn("Board not found");

        // Act & Assert
        final var exception = assertThrows(FunctionalException.class, () -> boardService.update(boardId, boardUpdate, image));
        assertEquals("Board not found", exception.getMessage());
        verify(boardRepository).findById(boardId);
    }

    @Test
    void shouldThrowExceptionWhenBoardNameIsDuplicateOnUpdate() throws NotFoundException {
        // Arrange
        final var boardId = 1L;
        final var boardUpdate = mock(BoardCreationUpdateDto.class);
        final var image = mock(MultipartFile.class);
        final var userId = 1L;
        final var workspaceId = 2L;
        final var boardName = "Duplicate Board";
        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardUpdate.workspaceId()).thenReturn(workspaceId);
        when(boardUpdate.name()).thenReturn(boardName);
        when(userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId))
                .thenReturn(Optional.of(mock(UserWorkspace.class)));
        when(boardRepository.findByNameAndWorkspace_Id(boardName, workspaceId))
                .thenReturn(Optional.of(mock(Board.class)));
        when(messages.get(GlobalConstants.BOARD_DUPLICATE_NAME_ERROR, boardName, workspaceId))
                .thenReturn("Board name is duplicate");

        // Act & Assert
        final var exception = assertThrows(FunctionalException.class, () -> boardService.update(boardId, boardUpdate, image));
        assertEquals("Board name is duplicate", exception.getMessage());
        verify(boardRepository).findByNameAndWorkspace_Id(boardName, workspaceId);
    }

    // Unit test for createColumn
    @Test
    void shouldCreateColumnSuccessfully() throws FunctionalException, NotFoundException {
        // Arrange
        final var boardColumnCreation = mock(BoardColumnCreationUpdateDto.class);
        final var boardId = 1L;
        final var organizationId = 10L;

        final var existingBoard = mock(Board.class);
        final var workspace = mock(Workspace.class);
        final var organization = mock(Organization.class);

        when(existingBoard.getColumns()).thenReturn(Collections.emptyList());
        when(existingBoard.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(boardColumnMapper.toBoardColumn(boardColumnCreation)).thenReturn(mock(BoardColumn.class));

        // Act
        boardService.createColumn(boardId, boardColumnCreation);

        // Assert
        verify(boardRepository).findById(boardId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
        verify(boardColumnMapper).toBoardColumn(boardColumnCreation);
        verify(boardColumnRepository).save(any());
    }

    // Unit test for updateColumn
    @Test
    void shouldUpdateColumnSuccessfully() throws FunctionalException, NotFoundException {
        // Arrange
        final var boardId = 1L;
        final var boardColumnId = 1L;
        final var organizationId = 10L;
        final var boardColumnUpdate = mock(BoardColumnCreationUpdateDto.class);

        final var existingBoardColumn = mock(BoardColumn.class);
        final var board = mock(Board.class);
        final var workspace = mock(Workspace.class);
        final var organization = mock(Organization.class);

        when(existingBoardColumn.getBoard()).thenReturn(board);
        when(board.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardColumnRepository.findById(boardColumnId)).thenReturn(Optional.of(existingBoardColumn));
        when(boardColumnMapper.toBoardColumn(boardColumnUpdate)).thenReturn(mock(BoardColumn.class));
        when(existingBoardColumn.getId()).thenReturn(boardColumnId);
        when(boardColumnRepository.save(existingBoardColumn)).thenReturn(existingBoardColumn);

        // Act
        boardService.updateColumn(boardId, boardColumnId, boardColumnUpdate);

        // Assert
        verify(boardColumnRepository).findById(boardColumnId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
        verify(boardColumnMapper).toBoardColumn(boardColumnUpdate);
        verify(boardColumnMapper).toUpdatedBoardColumn(eq(existingBoardColumn), any());
        verify(boardColumnRepository).save(existingBoardColumn);
    }

    // Unit test for deleteColumn
    @Test
    void shouldDeleteColumnSuccessfully() throws FunctionalException, NotFoundException {
        // Arrange
        final var boardId = 1L;
        final var boardColumnId = 1L;
        final var organizationId = 10L;

        final var existingBoardColumn = mock(BoardColumn.class);
        final var board = mock(Board.class);
        final var workspace = mock(Workspace.class);
        final var organization = mock(Organization.class);

        when(existingBoardColumn.getActions()).thenReturn(Collections.emptyList());
        when(existingBoardColumn.getBoard()).thenReturn(board);
        when(board.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardColumnRepository.findById(boardColumnId)).thenReturn(Optional.of(existingBoardColumn));

        // Act
        boardService.deleteColumn(boardId, boardColumnId);

        // Assert
        verify(boardColumnRepository).findById(boardColumnId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
        verify(boardColumnRepository).delete(existingBoardColumn);
    }

    @Test
    void shouldThrowExceptionWhenColumnHasActionsOnDelete() throws NotFoundException {
        // Arrange
        final var boardId = 1L;
        final var boardColumnId = 1L;
        final var organizationId = 10L;

        final var existingBoardColumn = mock(BoardColumn.class);
        final var board = mock(Board.class);
        final var workspace = mock(Workspace.class);
        final var organization = mock(Organization.class);

        when(existingBoardColumn.getActions()).thenReturn(List.of(mock(Action.class)));
        when(existingBoardColumn.getBoard()).thenReturn(board);
        when(board.getWorkspace()).thenReturn(workspace);
        when(workspace.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(boardColumnRepository.findById(boardColumnId)).thenReturn(Optional.of(existingBoardColumn));
        when(messages.get(GlobalConstants.ERROR_WS_COLUMN_NOT_EMPTY, ModuleEnum.BOARD.getName(), boardColumnId))
                .thenReturn("Column is not empty");

        // Act & Assert
        final var exception = assertThrows(FunctionalException.class, () -> boardService.deleteColumn(boardId, boardColumnId));
        assertEquals("Column is not empty", exception.getMessage());
        verify(boardColumnRepository).findById(boardColumnId);
        verify(userHelperService).verifyOrganizationAccess(organizationId);
    }

    // Unit test for getUserBoards
    @Test
    void shouldGetUserBoardsSuccessfully() throws NotFoundException {
        // Arrange
        final var userId = 1L;
        final var page = 0;
        final var size = 10;
        final var pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending().and(Sort.by("name").ascending()));
        final var boardPage = new PageImpl<Board>(List.of());

        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardRepository.findByUserBoards_User_Id(userId, pageable)).thenReturn(boardPage);

        // Act
        final var result = boardService.getUserBoards(null, page, size);

        // Assert
        verify(userHelperService).getConnectedUserDetails();
        verify(boardRepository).findByUserBoards_User_Id(userId, pageable);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() throws NotFoundException {
        // Arrange
        final var page = 0;
        final var size = 10;

        when(userHelperService.getConnectedUserDetails()).thenThrow(new NotFoundException("User not found"));

        // Act & Assert
        final var exception = assertThrows(NotFoundException.class, () -> boardService.getUserBoards(null, page, size));
        assertEquals("User not found", exception.getMessage());
        verify(userHelperService).getConnectedUserDetails();
    }

    @Test
    void shouldGetUserBoardsAsAdminSuccessfully() throws NotFoundException {
        // Arrange
        final var page = 0;
        final var size = 10;
        final var organizationId = 1L;
        final var pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending().and(Sort.by("name").ascending()));
        final var boardPage = new PageImpl<Board>(List.of(mock(Board.class)));

        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.role()).thenReturn(UserRoleEnum.ADMIN);
        when(connectedUser.organization()).thenReturn(mock(OrganizationDto.class));
        when(connectedUser.organization().id()).thenReturn(organizationId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardRepository.findByWorkspace_Organization_Id(organizationId, pageable)).thenReturn(boardPage);

        // Act
        final var result = boardService.getUserBoards(null, page, size);

        // Assert
        verify(userHelperService).getConnectedUserDetails();
        verify(boardRepository).findByWorkspace_Organization_Id(organizationId, pageable);
        assertEquals(boardPage.getContent().size(), result.getContent().size());
    }

    @Test
    void shouldGetUserBoardsWithWorkspaceIdSuccessfully() throws NotFoundException {
        // Arrange
        final var userId = 1L;
        final var workspaceId = 2L;
        final var page = 0;
        final var size = 10;
        final var pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending().and(Sort.by("name").ascending()));
        final var boardPage = new PageImpl<>(List.of(mock(Board.class)));

        final var connectedUser = mock(UserPrivilegesDto.class);

        when(connectedUser.userId()).thenReturn(userId);
        when(userHelperService.getConnectedUserDetails()).thenReturn(connectedUser);
        when(boardRepository.findByUserBoards_User_IdAndWorkspace_Id(userId, workspaceId, pageable)).thenReturn(boardPage);

        // Act
        final var result = boardService.getUserBoards(workspaceId, page, size);

        // Assert
        verify(userHelperService).getConnectedUserDetails();
        verify(boardRepository).findByUserBoards_User_IdAndWorkspace_Id(userId, workspaceId, pageable);
        assertEquals(boardPage.getContent().size(), result.getContent().size());
    }
}
