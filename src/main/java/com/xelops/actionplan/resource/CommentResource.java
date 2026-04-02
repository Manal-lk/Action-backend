package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.comment.CommentDto;
import com.xelops.actionplan.dto.comment.CreateCommentDto;
import com.xelops.actionplan.dto.comment.UpdateCommentDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.CommentService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentResource {

    private final CommentService commentService;

    @Operation(
            summary = "Create a comment for an action",
            description = "Create a comment for an action",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @PostMapping("/actions/{actionId}/comments")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.COMMENT)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long actionId, @Valid @RequestBody CreateCommentDto createCommentDto) throws NotFoundException {
        return commentService.createComment(createCommentDto, actionId);
    }

    @Operation(
            summary = "Update a comment",
            description = "Update a comment",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @PutMapping("/actions/{actionId}/comments/{commentId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.COMMENT)
    public CommentDto updateComment(@PathVariable Long actionId, @PathVariable Long commentId, @Valid @RequestBody UpdateCommentDto updateCommentDto) throws NotFoundException, FunctionalException {
        return commentService.updateComment(updateCommentDto, actionId, commentId);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @DeleteMapping("/actions/{actionId}/comments/{commentId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.COMMENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long actionId, @PathVariable Long commentId) throws NotFoundException, FunctionalException {
        commentService.deleteComment(actionId, commentId);
    }

    @Operation(
            summary = "Get all comments for an action",
            description = "Get all comments for an action",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @GetMapping("/actions/{actionId}/comments")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.COMMENT)
    public List<CommentDto> getCommentsByActionId(@PathVariable Long actionId) throws NotFoundException {
        return commentService.getCommentsByActionId(actionId);
    }
}
