package com.xelops.actionplan.client;

import com.xelops.actionplan.client.config.ClientConfig;
import com.xelops.actionplan.dto.notification.NewNotificationsDto;
import com.xelops.actionplan.dto.notification.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "notification-client", url = "${actionplan-platform.notification.hostname}", configuration = ClientConfig.class, path = "/v1/notifications")
public interface NotificationClient {
    @PostMapping
    void create(@RequestBody NewNotificationsDto newNotifications);

    @GetMapping("/{recipientId}")
    List<NotificationDto> listByRecipient(@PathVariable String recipientId);
}
