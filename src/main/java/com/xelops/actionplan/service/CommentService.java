package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.Comment;
import com.xelops.actionplan.domain.User;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ActionRepository actionRepository;
    private final UserService userService;
    private final Messages messages;
    private final UserBoardRepository userBoardRepository;
    private final UserHelperService userHelperService;

    @Transactional
    public CommentDto createComment(CreateCommentDto createCommentDto, Long actionId) throws NotFoundException {
        log.info("Start service createComment for actionId: {}", actionId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_ACTION_NOT_FOUND, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        userHelperService.hasAccessToBoard(action.getBoardColumn().getBoard().getId());
        User currentUser = userHelperService.getConnectedUser();
        Comment comment = commentMapper.createCommentDtoToComment(createCommentDto);
        comment.setAction(action);
        comment.setUser(currentUser);
        comment.setCreatedBy(currentUser);
        Comment savedComment = commentRepository.save(comment);
        CommentDto result = commentMapper.commentToCommentDto(savedComment);
        log.info("End service createComment for actionId: {}", actionId);
        return result;
    }

    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long actionId, Long commentId) throws NotFoundException, FunctionalException {
        log.info("Start service updateComment for actionId: {} and commentId: {}", actionId, commentId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_ACTION_NOT_FOUND, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        userHelperService.hasAccessToBoard(action.getBoardColumn().getBoard().getId());
        User currentUser = userHelperService.getConnectedUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_COMMENT_NOT_FOUND, commentId)));
        if (!comment.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new FunctionalException(messages.get(GlobalConstants.USER_CANNOT_EDIT_COMMENT_ERROR));
        }
        comment.setMessage(updateCommentDto.getMessage());
        Comment savedComment = commentRepository.save(comment);
        CommentDto result = commentMapper.commentToCommentDto(savedComment);
        log.info("End service updateComment for actionId: {} and commentId: {}", actionId, commentId);
        return result;
    }

    @Transactional
    public void deleteComment(Long actionId, Long commentId) throws NotFoundException, FunctionalException {
        log.info("Start service deleteComment for actionId: {} and commentId: {}", actionId, commentId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_ACTION_NOT_FOUND, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        userHelperService.hasAccessToBoard(action.getBoardColumn().getBoard().getId());
        User currentUser = userHelperService.getConnectedUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_COMMENT_NOT_FOUND, commentId)));
        if (!comment.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new FunctionalException(messages.get(GlobalConstants.USER_CANNOT_DELETE_COMMENT_ERROR));
        }
        comment.setDeleted(true);
        commentRepository.save(comment);
        log.info("End service deleteComment for actionId: {} and commentId: {}", actionId, commentId);
    }

    public List<CommentDto> getCommentsByActionId(Long actionId) throws NotFoundException {
        log.info("Start service getCommentsByActionId for actionId: {}", actionId);

        // SECURITY CHECK FIRST: Fetch and verify organization access immediately
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ERROR_WS_ACTION_NOT_FOUND, actionId)));
        userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        userHelperService.hasAccessToBoard(action.getBoardColumn().getBoard().getId());
        List<Comment> comments = commentRepository.findAllByActionIdAndDeletedIsFalse(actionId);
        List<CommentDto> result = commentMapper.commentsToCommentDtos(comments);
        log.info("End service getCommentsByActionId for actionId: {}", actionId);
        return result;
    }
}
