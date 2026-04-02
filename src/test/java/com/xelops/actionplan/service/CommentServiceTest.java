package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.*;
import com.xelops.actionplan.dto.comment.CommentDto;
import com.xelops.actionplan.dto.comment.CreateCommentDto;
import com.xelops.actionplan.dto.comment.UpdateCommentDto;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.CommentMapper;
import com.xelops.actionplan.repository.ActionRepository;
import com.xelops.actionplan.repository.CommentRepository;
import com.xelops.actionplan.repository.UserBoardRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private UserService userService;

    @Mock
    private Messages messages;

    @Mock
    private UserBoardRepository userBoardRepository;

    @Mock
    private UserHelperService userHelperService;

    @InjectMocks
    private CommentService commentService;

    private Action createActionWithOrganization(Long actionId, Long boardId, Long organizationId) {
        Action action = new Action();
        action.setId(actionId);
        Board board = new Board();
        board.setId(boardId);
        Workspace workspace = new Workspace();
        Organization organization = new Organization();
        organization.setId(organizationId);
        workspace.setOrganization(organization);
        board.setWorkspace(workspace);
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setBoard(board);
        action.setBoardColumn(boardColumn);
        return action;
    }

    @Test
    void createComment_success() throws NotFoundException {
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        Long actionId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = new Action();
        action.setId(actionId);
        Board board = new Board();
        board.setId(1L);
        Workspace workspace = new Workspace();
        Organization organization = new Organization();
        organization.setId(organizationId);
        workspace.setOrganization(organization);
        board.setWorkspace(workspace);
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setBoard(board);
        action.setBoardColumn(boardColumn);
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), board.getId())).thenReturn(true);
        when(commentMapper.createCommentDtoToComment(createCommentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.commentToCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(createCommentDto, actionId);

        assertNotNull(result);
    }

    @Test
    void createComment_actionNotFound() throws NotFoundException {
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        Long actionId = 1L;

        when(actionRepository.findById(actionId)).thenReturn(Optional.empty());
        when(messages.get(GlobalConstants.ERROR_WS_ACTION_NOT_FOUND, actionId)).thenReturn("Action not found");

        assertThrows(NotFoundException.class, () -> commentService.createComment(createCommentDto, actionId));
    }

    @Test
    void createComment_userNotInBoard() throws NotFoundException {
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        Long actionId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = new Action();
        action.setId(actionId);
        Board board = new Board();
        board.setId(1L);
        Workspace workspace = new Workspace();
        Organization organization = new Organization();
        organization.setId(organizationId);
        workspace.setOrganization(organization);
        board.setWorkspace(workspace);
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setBoard(board);
        action.setBoardColumn(boardColumn);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), board.getId())).thenReturn(false);
        when(messages.get(GlobalConstants.USER_DOES_NOT_HAVE_ACCESS_TO_BOARD_ERROR)).thenReturn("User does not have access to board");

        assertThrows(NotFoundException.class, () -> commentService.createComment(createCommentDto, actionId));
    }

    @Test
    void updateComment_success() throws NotFoundException, FunctionalException {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = new Action();
        action.setId(actionId);
        Board board = new Board();
        board.setId(1L);
        Workspace workspace = new Workspace();
        Organization organization = new Organization();
        organization.setId(organizationId);
        workspace.setOrganization(organization);
        board.setWorkspace(workspace);
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setBoard(board);
        action.setBoardColumn(boardColumn);
        Comment comment = new Comment();
        comment.setCreatedBy(user);
        CommentDto commentDto = new CommentDto();

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), board.getId())).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.commentToCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(updateCommentDto, actionId, commentId);

        assertNotNull(result);
    }

    @Test
    void updateComment_commentNotFound() throws NotFoundException {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        when(messages.get("error.ws.commentNotFound", commentId)).thenReturn("Comment not found");

        assertThrows(NotFoundException.class, () -> commentService.updateComment(updateCommentDto, actionId, commentId));
    }

    @Test
    void updateComment_userNotCreator() throws NotFoundException {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        User creator = new User();
        creator.setId(2L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);
        Comment comment = new Comment();
        comment.setCreatedBy(creator);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(messages.get(GlobalConstants.USER_CANNOT_EDIT_COMMENT_ERROR)).thenReturn("User cannot edit this comment");

        assertThrows(FunctionalException.class, () -> commentService.updateComment(updateCommentDto, actionId, commentId));
    }

    @Test
    void deleteComment_success() throws NotFoundException, FunctionalException {
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);
        Comment comment = new Comment();
        comment.setCreatedBy(user);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(actionId, commentId);

        assertTrue(comment.isDeleted());
    }

    @Test
    void deleteComment_commentNotFound() throws NotFoundException {
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        when(messages.get("error.ws.commentNotFound", commentId)).thenReturn("Comment not found");

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(actionId, commentId));
    }

    @Test
    void deleteComment_userNotCreator() throws NotFoundException {
        Long actionId = 1L;
        Long commentId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        User creator = new User();
        creator.setId(2L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);
        Comment comment = new Comment();
        comment.setCreatedBy(creator);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(messages.get(GlobalConstants.USER_CANNOT_DELETE_COMMENT_ERROR)).thenReturn("User cannot delete this comment");

        assertThrows(FunctionalException.class, () -> commentService.deleteComment(actionId, commentId));
    }

    @Test
    void getCommentsByActionId_success() throws NotFoundException {
        Long actionId = 1L;
        Long organizationId = 10L;
        User user = new User();
        user.setId(1L);
        Action action = createActionWithOrganization(actionId, 1L, organizationId);
        List<Comment> comments = List.of(new Comment());
        List<CommentDto> commentDtos = List.of(new CommentDto());

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doNothing().when(userHelperService).verifyOrganizationAccess(organizationId);
        when(userHelperService.getConnectedUser()).thenReturn(user);
        when(userBoardRepository.existsByUserIdAndBoardId(user.getId(), 1L)).thenReturn(true);
        when(commentRepository.findAllByActionIdAndDeletedIsFalse(actionId)).thenReturn(comments);
        when(commentMapper.commentsToCommentDtos(comments)).thenReturn(commentDtos);

        List<CommentDto> result = commentService.getCommentsByActionId(actionId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
