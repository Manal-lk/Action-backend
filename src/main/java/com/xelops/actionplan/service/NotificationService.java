package com.xelops.actionplan.service;

import com.xelops.actionplan.client.NotificationClient;
import com.xelops.actionplan.dto.notification.NotificationDto;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {
    private final NotificationClient notificationClient;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUserByRule(Predicate<Principal> rule) {
        log.info("Start service notifyUserByRule");
        final var loggedInUsers = simpUserRegistry.getUsers();
        loggedInUsers.stream()
                .map(SimpUser::getPrincipal)
                .filter(Objects::nonNull)
                .filter(rule)
                .map(Principal::getName)
                .forEach(this::notifyUser);
        log.info("End service notifyUserByRule");
    }

    private List<NotificationDto> listByRecipient(String recipient) throws NotFoundException {
        return notificationClient.listByRecipient(recipient);
    }

    public void notifyUser(String userAuthId) {
        log.info("Start service notifyUser | userAuthId: {}", userAuthId);
        final List<NotificationDto> notifications;
        try {
            notifications = listByRecipient(userAuthId);
        } catch (NotFoundException e) {
            log.error("No user found while notifying user", e);
            return;
        }

        messagingTemplate.convertAndSendToUser(
                userAuthId,
                GlobalConstants.NOTIFICATION_QUEUE,
                notifications
        );
        log.info("End service notifyUser | userAuthId: {}", userAuthId);
    }
}
