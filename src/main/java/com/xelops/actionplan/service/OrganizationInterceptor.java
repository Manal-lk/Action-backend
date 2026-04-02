package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.OrganizationRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OrganizationInterceptor implements HandlerInterceptor {

    private final OrganizationRepository organizationRepository;
    private final UserHelperService userHelperService;
    private final Messages messages;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws NotFoundException {

//        if (!userHelperService.isSuperAdmin()) {
//            final var connectedUser = userHelperService.getConnectedUser();
//            final var organizationId = connectedUser.getOrganization().getId();
//            organizationRepository.findById(organizationId)
//                    .orElseThrow(() -> new NotFoundException(
//                            messages.get(
//                                    GlobalConstants.ERROR_WS_NOT_FOUND,
//                                    ModuleEnum.ORGANIZATION.getName(),
//                                    organizationId
//                            )
//                    ));
//        }
        return true;
    }
}
