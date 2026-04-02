package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.WorkspaceDto;
import com.xelops.actionplan.dto.WorkspaceListingDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.WorkspaceService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/workspaces")
public class WorkspaceResource {
    private final WorkspaceService workspaceService;

    @Operation(
            summary = "Get all workspaces for current user",
            description = "Retrieve a list of all workspaces for current user",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of workspaces retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceListingDto.class))
                    )
            }
    )
    @GetMapping
    @ActionPlanPlatformLogger(module = ModuleEnum.WORKSPACE)
    public Page<WorkspaceListingDto> getWorkspacesForCurrentUser(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) throws NotFoundException {
        return workspaceService.getWorkspacesForCurrentUser(page, size);
    }


    @Operation(
            summary = "Create/Update a new workspace",
            description = "Create/Update a new workspace for the current organization",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Workspace created/Updated successfully"
                    )
            }
    )
    @PostMapping
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.WORKSPACE)
    public void createUpdateWorkspace(@Valid @RequestBody WorkspaceDto workspaceDto) throws NotFoundException {
        workspaceService.createUpdateWorkspace(workspaceDto);
    }

    @Operation(
            summary = "Get workspace by ID",
            description = "Retrieve a workspace by its ID",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Workspace retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Workspace not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.WORKSPACE)
    public WorkspaceDto getWorkspaceById(@PathVariable Long id) throws NotFoundException {
        return workspaceService.getWorkspaceDtoById(id);
    }
}
