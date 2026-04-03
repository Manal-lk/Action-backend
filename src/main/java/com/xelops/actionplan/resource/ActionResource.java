package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.exception.StorageException;
import com.xelops.actionplan.service.ActionService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping
public class ActionResource {

    private final ActionService actionService;

    @Operation(
            summary = "Create/Update an action",
            description = "Create/Update an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @PostMapping("/boards/{boardId}/actions")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public void createUpdateAction(@PathVariable Long boardId, @RequestBody @Valid ActionDto action) throws FunctionalException, NotFoundException {
        actionService.createUpdateAction(boardId, action);
    }

    @Operation(
            summary = "Get an action",
            description = "Get an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/boards/{boardId}/actions/{actionId}")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public ActionDto getAction(@PathVariable Long boardId, @PathVariable Long actionId) throws NotFoundException {
        return actionService.getAction(boardId, actionId);
    }

    @Operation(
            summary = "Delete an action",
            description = "Delete an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @DeleteMapping("/boards/{boardId}/actions/{actionId}")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public void deleteAction(@PathVariable Long boardId, @PathVariable Long actionId) throws NotFoundException {
        actionService.deleteAction(boardId, actionId);
    }

    @Operation(
            summary = "Move an action",
            description = "Move an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @PutMapping("/boards/{boardId}/actions/{actionId}/move")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public void moveAction(@PathVariable Long boardId,
                           @PathVariable Long actionId,
                           @RequestParam Long fromColumnId,
                           @RequestParam Long toColumnId,
                           @RequestParam(required = false) Integer insertAtIndex
    ) throws NotFoundException {
        actionService.moveAction(boardId, actionId, fromColumnId, toColumnId, insertAtIndex);
    }

    @Operation(
            summary = "Toggle action completion",
            description = "Toggle action completion status",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @PutMapping("/boards/{boardId}/actions/{actionId}/toggle-completion")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public void toggleActionCompletion(@PathVariable Long boardId,
                                       @PathVariable Long actionId,
                                       @RequestParam Boolean completed
    ) throws NotFoundException {
        actionService.toggleActionCompletion(boardId, actionId, completed);
    }

    @Operation(
            summary = "Get action statistics",
            description = "Get statistics for actions (total, completedRate, overdue, completion rate)",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/statistics-overview")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public ActionStatisticsDto getActionStatisticsProjection() throws NotFoundException {
        return actionService.getActionStatisticsProjection();
    }

    @Operation(
            summary = "Get action status breakdown",
            description = "Get breakdown of action statuses (in progress, completedRate, to do, blocked rate)",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/status-breakdown")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public ActionStatusBreakdownDto getActionStatusBreakdown() throws NotFoundException {
        return actionService.getActionStatusBreakdown();
    }

    @Operation(
            summary = "Get overdue actions breakdown",
            description = "Get breakdown of overdue actions by delay duration (1-3, 4-7, 8-14, 15+ days)",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/overdue-breakdown")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public OverdueActionsBreakdownDto getOverdueActionsBreakdown() throws NotFoundException {
        return actionService.getOverdueActionsBreakdown();
    }

    @Operation(
            summary = "Get average resolution time",
            description = "Get the average resolution time (in days) for completedRate actions.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/average-resolution-time")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public AverageResolutionTimeDto getAverageResolutionTime() throws NotFoundException {
        return actionService.getAverageResolutionTime();
    }

    @Operation(
            summary = "Get top assignees",
            description = "Ranking list showing assignee name and number of completedRate actions. Identifies key contributors and workload distribution.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/top-assignees")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<TopAssigneeDto> getTopAssignees() throws NotFoundException {
        return actionService.getTopAssignees();
    }

    @Operation(
            summary = "Get upcoming deadlines",
            description = "List of actions with upcoming due dates (next 7 days): Action Name and Days Remaining.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/upcoming-deadlines")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<UpcomingDeadlineDto> getUpcomingDeadlines() throws NotFoundException {
        return actionService.getUpcomingDeadlines();
    }

    @Operation(
            summary = "Get completion rate by priority",
            description = "Pie chart showing completion rate per priority (High, Medium, Low)",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/completion-rate-by-priority")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<CompletionRateByPriorityDto> getCompletionRateByPriority() throws NotFoundException {
        return actionService.getCompletionRateByPriority();
    }

    @Operation(
            summary = "Get actions trend",
            description = "Line chart displaying actions created and completedRate monthly.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/actions/trend")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<ActionsTrendDto> getActionsTrend() throws NotFoundException {
        return actionService.getActionsTrend();
    }

    @Operation(
            summary = "Add action attachments",
            description = "Add attachments to an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @PostMapping("/boards/{boardId}/actions/{actionId}/attachments")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<AttachmentDto> addActionAttachments(@PathVariable Long boardId,
                                                    @PathVariable Long actionId,
                                                    @RequestPart("files") MultipartFile[] files
    ) throws NotFoundException, StorageException {
        return actionService.addActionAttachments(boardId, actionId, files);
    }

    @Operation(
            summary = "Get action attachments",
            description = "Get all attachments for an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/boards/{boardId}/actions/{actionId}/attachments")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<AttachmentDto> getActionAttachments(@PathVariable Long boardId,
                                                    @PathVariable Long actionId
    ) throws NotFoundException {
        return actionService.getActionAttachments(boardId, actionId);
    }

    @Operation(
            summary = "Get action history",
            description = "Get the history of changes for an action",
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
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @GetMapping("/boards/{boardId}/actions/{actionId}/history")
    @ActionPlanPlatformLogger(module = ModuleEnum.ACTION)
    public List<HistoryDto> getActionHistory(@PathVariable Long boardId,
                                             @PathVariable Long actionId
    ) throws NotFoundException {
        return actionService.getActionHistory(boardId, actionId);
    }
}
