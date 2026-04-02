package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.UserInvitationDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserInvitationService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user-invitations")
public class UserInvitationResource {

    private final UserInvitationService userInvitationService;

    @Operation(
            summary = "Create a new users invitation",
            description = "Create a new users invitation for a workspace or board",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User invitations created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected Error",
                    content = @Content
            )
    })
    @PostMapping
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.USER_INVITATION)
    public void createInvitation(@Valid @RequestBody UserInvitationDto userInvitationDto) throws FunctionalException, NotFoundException {
        userInvitationService.createInvitation(userInvitationDto);
    }

    @Operation(
            summary = "Update user invitation profile",
            description = "Update the profile of a pending user invitation",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User invitation profile updated successfully"
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
    //@RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.USER_INVITATION)
    public void updateUserInvitationProfile(
            @RequestParam String email,
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) Long workspaceId,
            @RequestParam UserProfileEnum profile) throws FunctionalException {
        userInvitationService.updateUserInvitationProfile(email, boardId, workspaceId, profile);
    }

    @Operation(
            summary = "Validate user invitation by token and link user to board or workspace",
            description = "Validate invitation using token, then link user to board or workspace.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation validated and user linked successfully"),
            @ApiResponse(responseCode = "404", description = "Invitation not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected Error", content = @Content)
    })
    @PostMapping("/validate")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER_INVITATION)
    public void validateInvitation(@RequestBody String token) throws FunctionalException, NotFoundException {
        userInvitationService.validateAndLinkUserInvitation(token);
    }
}
