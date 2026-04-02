package com.xelops.actionplan.service;

import com.xelops.actionplan.client.NotificationClient;
import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Action;
import com.xelops.actionplan.domain.ActionMember;
import com.xelops.actionplan.domain.Board;
import com.xelops.actionplan.domain.User;
import com.xelops.actionplan.dto.notification.NewNotificationsDto;
import com.xelops.actionplan.dto.notification.NotificationDataDto;
import com.xelops.actionplan.enumeration.notification.NotificationTemplateEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTypeEnum;
import com.xelops.actionplan.enumeration.notification.PlatformEnum;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.ActionMemberRepository;
import com.xelops.actionplan.repository.ActionRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ActionMemberService {

    private final ActionMemberRepository actionMemberRepository;
    private final ActionRepository actionRepository;
    private final Messages messages;
    private final UserHelperService userHelperService;
    private final NotificationClient notificationClient;
    private final NotificationService notificationService;
    private final BoardHelperService boardHelperService;

    @Transactional
    public void addActionMembers(Long boardId, Long actionId, List<Long> userIds) throws NotFoundException {
        log.info("Start service addActionMembers | boardId: {} | actionId: {} | userIds: {}", boardId, actionId, userIds);
        final Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException(messages.get(GlobalConstants.ACTION_NOT_FOUND_ERROR, actionId)));

        List<Long> existingMembersIds = actionMemberRepository.findAllIdsByActionId(actionId);

        Collection<Long> membersToAdd = CollectionUtils.subtract(userIds, existingMembersIds);
        Collection<Long> membersToRemove = CollectionUtils.subtract(existingMembersIds, userIds);

        actionMemberRepository.deleteAll(membersToRemove.stream().map(id ->
                ActionMember.builder()
                        .id(id)
                        .build()
        ).toList());

        actionMemberRepository.saveAll(membersToAdd.stream().map(userId ->
                ActionMember.builder()
                        .action(Action.builder().id(actionId).build())
                        .member(User.builder().id(userId).build())
                        .build()
        ).toList());

        if (action.getAssignee() != null && !membersToAdd.contains(action.getAssignee().getId())) {
            membersToAdd.add(action.getAssignee().getId());
        }
        if (!membersToAdd.isEmpty()) {
            final Board board = boardHelperService.getById(boardId);
            Map<Long, User> users = userHelperService.findUsersByIds(new ArrayList<>(membersToAdd));
            final var notificationsData = membersToAdd.stream()
                    .map(user -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.APP)
                            .recipientEmail(users.get(user).getEmail())
                            .recipientId(users.get(user).getKeycloakId())
                            .titleData(Map.of())
                            .entityId(action.getId())
                            .redirectionPath("/boards/" + boardId)
                            .titleData(Map.of("actionName", action.getTitle()))
                            .bodyData(Map.of(
                                    "boardName", board.getName(),
                                    "dueDate", Objects.toString(action.getDueDate(), ""),
                                    "actionName", action.getTitle()
                            ))
                            .build()
                    )
                    .toList();
            final var emailNotificationsData = membersToAdd.stream()
                    .map(user -> NotificationDataDto.builder()
                            .type(NotificationTypeEnum.EMAIL)
                            .recipientEmail(users.get(user).getEmail())
                            .recipientId(users.get(user).getKeycloakId())
                            .titleData(Map.of("actionName", action.getTitle()))
                            .bodyData(Map.of(
                                    "boardName", board.getName(),
                                    "dueDate", Objects.toString(action.getDueDate(), ""),
                                    "actionName", action.getTitle()
                            ))
                            .build()
                    )
                    .toList();
            final var newNotification = NewNotificationsDto.builder()
                    .platformId(PlatformEnum.ACTIONS.name())
                    .template(NotificationTemplateEnum.USER_ASSIGNED_IN_ACTION_NOTIF)
                    .notificationsData(notificationsData)
                    .build();
            final var newEmailNotification = NewNotificationsDto.builder()
                    .platformId(PlatformEnum.ACTIONS.name())
                    .template(NotificationTemplateEnum.USER_ASSIGNED_IN_ACTION)
                    .notificationsData(emailNotificationsData)
                    .build();
            try {
                notificationClient.create(newNotification);
                notificationClient.create(newEmailNotification);
                notificationsData.stream()
                        .map(NotificationDataDto::recipientId)
                        .forEach(notificationService::notifyUser);
            } catch (Exception e) {
                log.error("Error Service : Sending notification", e);
            }
        }

        log.info("End service addActionMembers | boardId: {} | actionId: {} | userIds: {}", boardId, actionId, userIds);
    }

    public void removeActionMembers(Long actionId) throws NotFoundException {
        log.info("Start service removeActionMembers | actionId: {}", actionId);

        // Verify organization access to action (defense in depth)
        Action action = actionRepository.findById(actionId)
                .orElse(null);
        if (action != null) {
            userHelperService.verifyOrganizationAccess(action.getBoardColumn().getBoard().getWorkspace().getOrganization().getId());
        }

        List<ActionMember> actionMembers = actionMemberRepository.findAllByActionId(actionId);
        if (CollectionUtils.isNotEmpty(actionMembers)) {
            actionMemberRepository.deleteAll(actionMembers);
        }
        log.info("End service removeActionMembers | actionId: {}", actionId);
    }
}
