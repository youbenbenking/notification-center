package com.notification.service;

import java.util.List;

import com.notification.model.NotificationLog;

public interface NotificationService {

    NotificationLog save(NotificationLog notificationLog);

    List<NotificationLog> queryMessages(String srcAppCode, String userName);
}
