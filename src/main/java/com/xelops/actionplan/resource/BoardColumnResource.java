package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.BoardColumnSimplifiedDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.BoardColumnService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping
public class BoardColumnResource {

    private final BoardColumnService boardColumnService;

    @Operation(
            summary = "Get columns of a board",
            description = "Get columns of a board",
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
    @GetMapping("/boards/{boardId}/columns/simplified")
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD_COLUMN)
    public List<BoardColumnSimplifiedDto> getSimplifiedBoardColumnsByBoardId(@PathVariable Long boardId) throws NotFoundException {
        return boardColumnService.getSimplifiedBoardColumnsByBoardId(boardId);
    }

    @Operation(
            summary = "Move a column",
            description = "Move a column",
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
    @PutMapping("/boards/{boardId}/columns/{columnId}/move")
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD_COLUMN)
    public void moveColumn(
            @PathVariable Long boardId,
            @PathVariable Long columnId,
            @RequestParam Integer insertAtIndex
    ) throws NotFoundException {
        boardColumnService.moveColumn(boardId, columnId, insertAtIndex);
    }
}
