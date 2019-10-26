package com.notification.service;

import java.util.List;

import com.notification.domain.model.NotificationLog;

public interface NotificationService {

    Long save(NotificationLog notificationLog);

    List<NotificationLog> queryMessages(String srcAppCode, String userName);
}
