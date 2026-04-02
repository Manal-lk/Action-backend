package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.UserDto;
import com.xelops.actionplan.dto.UserPrivilegesDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserService;
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

@RestController
@RequestMapping("/user-details")
@RequiredArgsConstructor
public class UserDetailsResource {

    private final UserService userService;

    @Operation(
            summary = "Get and Create Connected User If Not Exist",
            description = "Get details/privileges and Create Connected User If Not Exist",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserPrivilegesDto.class)
                            )
                    ),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @PostMapping
    @ActionPlanPlatformLogger(module = ModuleEnum.USER)
    public UserPrivilegesDto getAndCreateConnectedUserIfNotExist() throws FunctionalException, NotFoundException {
        return userService.getAndCreateConnectedUserIfNotExist();
    }

    @Operation(
            summary = "Search Users for Workspace or Board",
            description = "Search users by fullName from organization that are not in the specified workspace or board (autocomplete)",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @GetMapping("/search")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER)
    public List<UserDto> searchUsers(
            @RequestParam String fullName,
            @RequestParam(required = false) Long workspaceId,
            @RequestParam(required = false) Long boardId
    ) throws FunctionalException, NotFoundException {
        return userService.searchUsersForWorkspaceOrBoard(fullName, workspaceId, boardId);
    }


    @Operation(
            summary = "Search Users in Workspace",
            description = "Search Users in Workspace",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @GetMapping("/search/in-workspace")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER)
    public List<UserDto> searchUsersInWorkspace(
            @RequestParam String fullName,
            @RequestParam Long workspaceId
    ) throws NotFoundException {
        return userService.searchUsersInWorkspace(fullName, workspaceId);
    }
}
