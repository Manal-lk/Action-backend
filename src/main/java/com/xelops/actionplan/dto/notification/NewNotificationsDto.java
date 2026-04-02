package com.xelops.actionplan.dto.notification;


import com.xelops.actionplan.enumeration.notification.NotificationTemplateEnum;
import lombok.Builder;

import java.util.List;

@Builder
public record NewNotificationsDto(
        NotificationTemplateEnum template,
        String platformId,
        List<NotificationDataDto> notificationsData
) {}
