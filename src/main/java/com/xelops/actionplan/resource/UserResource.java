package com.xelops.actionplan.resource;

import com.xelops.actionplan.dto.UserFilterRequestDto;
import com.xelops.actionplan.dto.UserSimplifiedDto;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.UserListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FIX : Ce fichier REMPLACE AppUserResource.java (qui causait une erreur "Duplicate class").
 * L'endpoint /app-users/filter est intégré ici directement,
 * dans le même package, sans créer de doublon.
 */
@RestController
@RequestMapping("/app-users")
@RequiredArgsConstructor
public class UserResource {

    private final UserListService userListService;

    @Operation(
            summary = "Filter Users",
            description = "Retourne la liste des utilisateurs de l'organisation avec filtres optionnels",
            security = @SecurityRequirement(name = GlobalConstants.SECURITY_SCHEME_NAME)
    )
    @PostMapping("/filter")
    @ActionPlanPlatformLogger(module = ModuleEnum.USER)
    public List<UserSimplifiedDto> filterUsers(
            @RequestBody(required = false) UserFilterRequestDto filterRequest
    ) throws NotFoundException {
        return userListService.filterUsers(filterRequest);
    }
}