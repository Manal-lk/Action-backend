package com.xelops.actionplan.dto.notification;

import com.xelops.actionplan.enumeration.notification.NotificationStatusEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTypeEnum;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationDto(
        Long id,
        String title,
        String body,
        NotificationStatusEnum status,
        String recipientEmail,
        String recipientId,
        NotificationTypeEnum type,
        LocalDateTime createdAt,
        String redirectionPath,
        Long entityId
        ) {
}
