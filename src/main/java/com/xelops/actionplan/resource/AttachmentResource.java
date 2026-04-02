package com.xelops.actionplan.resource;


import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.AttachmentDataDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.AttachmentService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AttachmentResource {

    private final AttachmentService attachmentService;

    @Operation(
            summary = "load attachment",
            description = "load attachment",
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
    @GetMapping("/boards/{boardId}/actions/{actionId}/attachments/{attachmentId}")
    @ActionPlanPlatformLogger(module = ModuleEnum.ATTACHMENT)
    public ResponseEntity<InputStreamResource> loadAttachment(
            @PathVariable Long boardId,
            @PathVariable Long actionId,
            @PathVariable Long attachmentId
    ) throws NotFoundException {

        AttachmentDataDto attachment = attachmentService.loadAttachment(boardId, actionId, attachmentId);
        InputStreamResource resource = new InputStreamResource(attachment.inputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.filename() + "\"")
                .contentType(attachment.contentType())
                .contentLength(attachment.size())
                .body(resource);
    }

    @Operation(
            summary = "delete an attachment",
            description = "delete an attachment",
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
    @DeleteMapping("/boards/{boardId}/actions/{actionId}/attachments/{attachmentId}")
    @ActionPlanPlatformLogger(module = ModuleEnum.ATTACHMENT)
    public void deleteAttachment(
            @PathVariable Long boardId,
            @PathVariable Long actionId,
            @PathVariable Long attachmentId
    ) throws NotFoundException {

        attachmentService.deleteAttachment(boardId, actionId, attachmentId);
    }
}
