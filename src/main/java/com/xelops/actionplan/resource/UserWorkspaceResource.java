package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.UserProfileDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserWorkspaceService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-workspaces")
@RequiredArgsConstructor
public class UserWorkspaceResource {

    private final UserWorkspaceService userWorkspaceService;

    @Operation(
            summary = "Get user profiles for a workspace",
            description = "Get user profiles for a workspace, including invited users",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @GetMapping("/{workspaceId}/members")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER_WORKSPACE)
    public List<UserProfileDto> getWorkspaceUsersAndInvitedUsers(@PathVariable Long workspaceId) throws NotFoundException {
        return userWorkspaceService.getWorkspaceUsersAndInvitedUsers(workspaceId);
    }

    @Operation(
            summary = "Update user workspace profile",
            description = "Update the profile of a user workspace association",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User workspace profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected Error",
                    content = @Content
            )
    })
    @PutMapping("/profile")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    public void updateUserWorkspaceProfile(
            @RequestParam Long workspaceId,
            @RequestParam Long userId,
            @RequestParam UserProfileEnum profile) throws FunctionalException, NotFoundException {
        userWorkspaceService.updateUserWorkspaceProfile(workspaceId, userId, profile);
    }
}
