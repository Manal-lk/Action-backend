package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.config.validation.ValidFile;
import com.xelops.actionplan.dto.*;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.BoardService;
import com.xelops.actionplan.utils.MimeType;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/boards")
public class BoardResource {

    private final BoardService boardService;

    @Operation(
            summary = "Create a Board",
            description = "Create a new Board",
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
    // TODO: Add authorization when permissions are defined
    @PostMapping
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public Long create(@RequestPart @Valid BoardCreationUpdateDto boardCreation, @RequestPart(required = false) @ValidFile(MimeType.IMAGE) MultipartFile image) throws FunctionalException, NotFoundException {
        return boardService.create(boardCreation, image);
    }

    @Operation(
            summary = "Update a Board",
            description = "Update an existing Board",
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
    @PatchMapping("/{boardId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public void update(@PathVariable Long boardId, @RequestPart @Valid BoardCreationUpdateDto boardUpdate, @RequestPart(required = false) @ValidFile(MimeType.IMAGE) MultipartFile image) throws FunctionalException, NotFoundException {
        boardService.update(boardId, boardUpdate, image);
    }

    @Operation(
            summary = "Create a Board Column",
            description = "Create a new Board Column",
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
    @PostMapping("/{boardId}/columns")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public void createColumn(@PathVariable Long boardId, @RequestBody @Valid BoardColumnCreationUpdateDto boardColumn) throws FunctionalException, NotFoundException {
        boardService.createColumn(boardId, boardColumn);
    }

    @Operation(
            summary = "Update a Board Column",
            description = "Update an existing Board Column",
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
    @PatchMapping("/{boardId}/columns/{boardColumnId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public void updateColumn(@PathVariable Long boardId, @PathVariable Long boardColumnId, @RequestBody @Valid BoardColumnCreationUpdateDto boardColumnUpdate) throws FunctionalException, NotFoundException {
        boardService.updateColumn(boardId, boardColumnId, boardColumnUpdate);
    }

    @Operation(
            summary = "Delete a Board Column",
            description = "Delete an existing Board Column",
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
    @DeleteMapping("/{boardId}/columns/{boardColumnId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public void deleteColumn(@PathVariable Long boardId, @PathVariable Long boardColumnId) throws FunctionalException, NotFoundException {
        boardService.deleteColumn(boardId, boardColumnId);
    }

    @Operation(
            summary = "Get user Boards",
            description = "Retrieve all Boards accessible to the connected user",
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
    @GetMapping
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public Page<BoardDto> getUserBoards(
            @RequestParam(value = "workspaceId", required = false) Long workspaceId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size
    ) throws NotFoundException {
        return boardService.getUserBoards(workspaceId, page, size);
    }

    @Operation(
            summary = "Retrieve all users who have access to the board",
            description = "Retrieve all users who have access to the board",
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
    @GetMapping("{boardId}/users")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public Page<UserSimplifiedDto> getBoardUsers(
            @PathVariable Long boardId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size
    ) throws FunctionalException, NotFoundException {
        return boardService.getBoardUsers(boardId, page, size);
    }

    @Operation(
            summary = "Get simplified Board by id",
            description = "Retrieve a simplified Board by its boardId",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unexpected Error", responseCode = "500")
            }
    )
    @GetMapping("/{boardId}")
    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public BoardSimplifiedDto getSimplifiedBoardById(@PathVariable Long boardId) throws NotFoundException {
        return boardService.getSimplifiedBoardById(boardId);
    }

    @Operation(
            summary = "Get all actions",
            description = "Retrieve a list of all actions",
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
    @PostMapping("/{boardId}/filter")
    @ActionPlanPlatformLogger(module = ModuleEnum.BOARD)
    public List<BoardColumnDto> getBoardColumnsByBoardId(@PathVariable Long boardId, @RequestBody(required = false) ActionFilterDto actionFilter) throws NotFoundException {
        return boardService.getBoardColumnsByBoardId(boardId, actionFilter);
    }
}