package com.xelops.actionplan.service;

import com.xelops.actionplan.client.NotificationClient;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.domain.UserInvitation;
import com.xelops.actionplan.dto.notification.NewNotificationsDto;
import com.xelops.actionplan.dto.notification.NotificationDataDto;
import com.xelops.actionplan.enumeration.notification.NotificationTemplateEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTypeEnum;
import com.xelops.actionplan.enumeration.notification.PlatformEnum;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInvitationNotificationService {

    private final NotificationClient notificationClient;
    private final NotificationService notificationService;
    private final OrganizationService organizationService;

    public void sendBoardInvitationsNotification(List<UserInvitation> invitations, List<User> users) {
        log.info("Start service sendBoardInvitationsNotification for invitations : {} : users: {}", invitations.size(), users.size());

        if (!CollectionUtils.isEmpty(users)) {
            List<String> emails = users.stream().map(User::getEmail).toList();
            List<NotificationDataDto> notifications = invitations.stream()
                    .filter(inv -> inv.getBoard() != null && emails.contains(inv.getEmail()))
                    .map(inv -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.APP)
                            .recipientId(users.stream().filter(u -> u.getEmail().equals(inv.getEmail())).findFirst().map(User::getKeycloakId).orElse(null))
                            .recipientEmail(inv.getEmail())
                            .redirectionPath(GlobalConstants.NOTIFICATION_BOARD_REDIRECTION_PATH + inv.getBoard().getId())
                            .titleData(Map.of(
                                    GlobalConstants.NOTIFICATION_BOARD_NAME_KEY, inv.getBoard().getName()
                            ))
                            .bodyData(Map.of(
                                    GlobalConstants.NOTIFICATION_BOARD_NAME_KEY, inv.getBoard().getName()
                            ))
                            .build())
                    .toList();

            if (!CollectionUtils.isEmpty(notifications)) {
                log.info("Sending app notifications for invited users to board | notifications size: {}", notifications.size());
                notificationClient.create(buildEmailNotification(notifications, NotificationTemplateEnum.USER_INVITED_TO_BOARD_NOTIF));
                notifications.stream()
                        .map(NotificationDataDto::recipientId)
                        .forEach(notificationService::notifyUser);
            }
        }
        List<NotificationDataDto> emails = invitations.stream()
                .filter(inv -> inv.getBoard() != null)
                .map(inv -> NotificationDataDto.builder()
                        .type(NotificationTypeEnum.EMAIL)
                        .recipientEmail(inv.getEmail())
                        .titleData(Map.of(
                                GlobalConstants.NOTIFICATION_BOARD_NAME_KEY, inv.getBoard().getName()
                        ))
                        .bodyData(Map.of(
                                GlobalConstants.NOTIFICATION_BOARD_NAME_KEY, inv.getBoard().getName(),
                                GlobalConstants.NOTIFICATION_APP_LINK_KEY, organizationService.getUrlPlatform() + "/boards/" + inv.getBoard().getId().toString() + "?token=" + inv.getToken()
                        ))
                        .build())
                .toList();

        if (!CollectionUtils.isEmpty(emails)) {
            log.info("Send email notifications for invited user to board | notifications size : {}", emails.size());
            notificationClient.create(buildEmailNotification(emails, NotificationTemplateEnum.USER_INVITED_TO_BOARD));
        }

        log.info("End service sendBoardInvitationsNotification for invitations : {} : users: {}", invitations.size(), users.size());
    }

    public void sendWorkspaceInvitationNotification(List<UserInvitation> invitations, List<User> users) {
        log.info("Start service sendWorkspaceInvitationNotification for invitations : {} : users: {}", invitations.size(), users.size());

        if (!CollectionUtils.isEmpty(users)) {
            List<String> emails = users.stream().map(User::getEmail).toList();
            List<NotificationDataDto> notifications = invitations.stream()
                    .filter(inv -> inv.getWorkspace() != null && emails.contains(inv.getEmail()))
                    .map(inv -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.APP)
                            .recipientId(users.stream().filter(u -> u.getEmail().equals(inv.getEmail())).findFirst().map(User::getKeycloakId).orElse(null))
                            .recipientEmail(inv.getEmail())
                            .redirectionPath(GlobalConstants.NOTIFICATION_WORKSPACE_REDIRECTION_PATH + inv.getWorkspace().getId())
                            .titleData(Map.of(
                                    GlobalConstants.NOTIFICATION_WORKSPACE_NAME_KEY, inv.getWorkspace().getName()
                            ))
                            .bodyData(Map.of(
                                    GlobalConstants.NOTIFICATION_WORKSPACE_NAME_KEY, inv.getWorkspace().getName()
                            ))
                            .build())
                    .toList();

            if (!CollectionUtils.isEmpty(notifications)) {
                log.info("Sending app notifications for invited users to workspace | notifications size: {}", notifications.size());
                notificationClient.create(buildEmailNotification(notifications, NotificationTemplateEnum.USER_INVITED_TO_WORKSPACE_NOTIF));
                notifications.stream()
                        .map(NotificationDataDto::recipientId)
                        .forEach(notificationService::notifyUser);
            }
        }

        List<NotificationDataDto> emails = invitations.stream()
                .filter(inv -> inv.getWorkspace() != null)
                .map(inv -> NotificationDataDto.builder()
                        .type(NotificationTypeEnum.EMAIL)
                        .recipientEmail(inv.getEmail())
                        .titleData(Map.of(
                                GlobalConstants.NOTIFICATION_WORKSPACE_NAME_KEY, inv.getWorkspace().getName()
                        ))
                        .bodyData(Map.of(
                                GlobalConstants.NOTIFICATION_WORKSPACE_NAME_KEY, inv.getWorkspace().getName(),
                                GlobalConstants.NOTIFICATION_APP_LINK_KEY, organizationService.getUrlPlatform() + "/board-management/?workspaceId=" + inv.getWorkspace().getId().toString() + "&token=" + inv.getToken()
                        ))
                        .build())
                .toList();

        if (!CollectionUtils.isEmpty(emails)) {
            log.info("Send email notifications for invited user to workspace | notifications size : {}", emails.size());
            notificationClient.create(buildEmailNotification(emails, NotificationTemplateEnum.USER_INVITED_TO_WORKSPACE));
        }
        log.info("End service sendWorkspaceInvitationNotification for invitations : {} : users: {}", invitations.size(), users.size());
    }


    private NewNotificationsDto buildEmailNotification(List<NotificationDataDto> data, NotificationTemplateEnum notificationTemplateEnum) {
        return NewNotificationsDto.builder()
                .platformId(PlatformEnum.ACTIONS.name())
                .template(notificationTemplateEnum)
                .notificationsData(data)
                .build();
    }
}
