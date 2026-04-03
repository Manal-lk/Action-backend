package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.UserFilterCriteriaDto;
import com.xelops.actionplan.dto.UserManagementDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserManagementService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the User Management page.
 *
 * Base path : /app-users
 */
@RestController
@RequestMapping("/user-management")
@RequiredArgsConstructor
public class UserManagementResource {

    private final UserManagementService userManagementService;

    /**
     * POST /app-users/filter
     *
     * Returns all users of the connected user's organisation.
     * The request body is optional – an empty body returns every user.
     * Filtering fields:
     *   - fullName  (String, partial match, case-insensitive)
     *   - role      (UserRoleEnum: SUPER_ADMIN | ADMIN | SIMPLE_USER)
     */
    @Operation(
            summary = "Filter users in organisation",
            description = "Returns the list of users belonging to the connected user's organisation, "
                    + "with optional filtering by fullName (partial, case-insensitive) and role.",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserManagementDto.class)
                            )
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @PostMapping("/management/filter")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER)
    public List<UserManagementDto> filterUsers(
            @RequestBody(required = false) UserFilterCriteriaDto criteria
    ) throws NotFoundException {
        return userManagementService.filterUsers(criteria);
    }
}