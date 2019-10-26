package com.notification.integration;

import java.util.ArrayList;
import java.util.List;

import com.notification.common.constant.LogTypeConstants;
import com.notification.common.util.JsonUtil;
import com.notification.domain.model.NotificationLog;
import com.notification.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = LogTypeConstants.INTEGRATION_LOGGER)
public class CacheMessageClient {

    @Autowired
    private RedisClient redisClient;

    public List<NotificationLog> loadNotification(String key) {
        try {
            String json = (String)redisClient.get(key);
            List<NotificationLog> notificationList = JsonUtil.jsonToList(json, NotificationLog.class);
            return notificationList;
        } catch (Exception e) {
            log.error("消息通知Redis获取缓存异常!", e);
            return new ArrayList();
        }
    }

    public void cacheNotification(String key, List<NotificationLog> notificationLogs) {
        if (CollectionUtils.isEmpty(notificationLogs)) {
            return;
        }
        try {
            redisClient.set(key, JsonUtil.objectToJson(notificationLogs), 86400);
        } catch (Exception e) {
            log.error("消息通知Redis获取缓存异常!", e);
        }
    }
}
