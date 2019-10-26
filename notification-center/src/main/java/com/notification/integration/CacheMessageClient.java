package com.notification.integration;

import java.util.ArrayList;
import java.util.List;

import com.notification.constant.LogTypeConstants;
import com.notification.model.NotificationLog;
import com.notification.redis.RedisClient;
import com.notification.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = LogTypeConstants.INTEGRATION_LOGGER)
public class CacheMessageClient {

    @Autowired
    private RedisClient redisClient;

    public List<NotificationLog> getNotification(String key) {
        if (StringUtils.isBlank(key)) {
            return new ArrayList();
        }
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
        try {
            redisClient.set(key, JsonUtil.objectToJson(notificationLogs), 86400);
        } catch (Exception e) {
            log.error("消息通知Redis获取缓存异常!", e);
        }
    }
}
