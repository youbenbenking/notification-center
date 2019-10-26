package com.notification.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.notification.common.constant.LogTypeConstants;
import com.notification.dao.NotificationLogMapper;
import com.notification.domain.model.NotificationLog;
import com.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = LogTypeConstants.SERVICE_LOGGER)
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationLogMapper notificationLogMapper;

	@Override
	public Long save(NotificationLog notificationLog) {
		notificationLogMapper.insertSelective(notificationLog);
		return notificationLog.getId();
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
