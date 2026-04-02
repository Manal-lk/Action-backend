package com.xelops.actionplan.dto.notification;


import com.xelops.actionplan.enumeration.notification.NotificationTemplateEnum;
import com.xelops.actionplan.enumeration.notification.NotificationTypeEnum;
import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationDataDto(
        String recipientEmail,
        String recipientId,
        NotificationTypeEnum type,
        Long entityId,
        String redirectionPath,
        NotificationTemplateEnum template,
        Map<String, String> titleData,
        Map<String, String> bodyData
) {
}
