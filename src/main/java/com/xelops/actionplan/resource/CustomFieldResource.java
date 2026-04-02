package com.xelops.actionplan.resource;

import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.CustomFieldOptionSimplifiedDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.service.CustomFieldService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/custom-fields")
public class CustomFieldResource {

    private final CustomFieldService customFieldService;

    @Operation(
            summary = "Get custom field options",
            description = "Get custom field options",
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
    @GetMapping("/{type}/options")
    @ActionPlanPlatformLogger(module = ModuleEnum.CUSTOM_FIELD)
    public List<CustomFieldOptionSimplifiedDto> getCustomFieldOption(@PathVariable String type) {
        return customFieldService.getCustomFieldOptions(type);
    }
}
