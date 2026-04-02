package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.TrelloImportRequestDto;
import com.xelops.actionplan.dto.TrelloImportResponseDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.TrelloImportService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/trello")
public class TrelloImportResource {

    private final TrelloImportService trelloImportService;

    @Operation(
            summary = "Import a Trello Board",
            description = "Import a complete Trello board with columns, actions, and members into the workspace",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            description = "Successful Operation - Returns board ID and import statistics",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request - Invalid data or duplicate board name",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Forbidden - User not in workspace",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "Not Found - Workspace does not exist",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Unexpected Error",
                            responseCode = "500"
                    )
            }
    )
    @PostMapping("/import")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public TrelloImportResponseDto importFromTrello(
            @RequestParam Long workspaceId,
            @RequestBody @Valid TrelloImportRequestDto trelloData
    ) throws FunctionalException, NotFoundException {
        return trelloImportService.importBoard(workspaceId, trelloData);
    }
}
