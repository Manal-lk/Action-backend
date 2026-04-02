package com.xelops.actionplan.resource;

import com.xelops.actionplan.client.NotificationClient;
import com.xelops.actionplan.config.logging.ActionPlanPlatformLogger;
import com.xelops.actionplan.dto.ApiError;
import com.xelops.actionplan.enumeration.ModuleEnum;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.service.NotificationService;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@EnableScheduling
public class NotificationResource {
    private final NotificationService notificationService;

    @SubscribeMapping(GlobalConstants.NOTIFICATION_QUEUE)
//    @RolesAllowed({UserRoleEnum.Fields.SIMPLE_USER, UserRoleEnum.Fields.ADMIN})
    @ActionPlanPlatformLogger(module = ModuleEnum.NOTIFICATION)
    public void notifyUsersOnSubscribe(Principal principal) {
        notificationService.notifyUser(principal.getName());
    }

    @MessageExceptionHandler
    @SendToUser(value = GlobalConstants.NOTIFICATION_ERROR_QUEUE)
    public ApiError handleException(Exception e) {
        return ApiError.builder()
                .message(e.getMessage())
                .errCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .build();
    }

    private final NotificationClient notificationClient;

    @GetMapping("test")
    public void test() {
        notificationClient.listByRecipient("386ff29a-8c48-4518-9630-ddbfa8882f73");
    }
}
