package com.notification.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.notification.dao.NotificationLogMapper;
import com.notification.model.NotificationLog;
import com.notification.service.NotificationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationLogMapper notificationLogMapper;

	@Override
	public NotificationLog save(NotificationLog notificationLog) {
		notificationLogMapper.insertSelective(notificationLog);
		return new NotificationLog();
	}

	@Override
	public List<NotificationLog> queryMessages(String srcAppCode, String userName) {
		List<NotificationLog> notificationLogs = notificationLogMapper.selectMessages(srcAppCode, userName);
		if (CollectionUtils.isEmpty(notificationLogs)) {
			return new ArrayList<>();
		}
		return notificationLogs;
	}
}
