package com.notification.controller;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.notification.constant.LogTypeConstants;
import com.notification.convert.NotificationConvertor;
import com.notification.integration.CacheMessageClient;
import com.notification.integration.MQMessageClient;
import com.notification.model.NotificationLog;
import com.notification.model.NotificationMessage;
import com.notification.response.ResponseWrapper;
import com.notification.service.NotificationService;
import com.notification.validate.NotificationValidator;
import com.notification.vo.RestfulParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息通知处理类
 *
 * @author youben
 */
@RestController
@RequestMapping("/api")
@Slf4j(topic = LogTypeConstants.WEB_LOGGER)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationConvertor notificationConvertor;
    @Autowired
    private NotificationValidator notificationValidator;
    @Autowired
    private MQMessageClient mqMessageClient;
    @Autowired
    private CacheMessageClient cacheMessageClient;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 30,
        TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    @RequestMapping(value = "/appUserMsg", method = RequestMethod.POST)
    public ResponseWrapper receive(@RequestBody RestfulParamVo<NotificationMessage> param) {
        notificationValidator.validateReceive(param);
        //消息入库,默认[待发送]
        Long notificationLogId = notificationService.save(notificationConvertor.convertToDto(param));
        //发送消息队列,异步解耦,提高并发(即使入队失败不应该影响消息入库)
        mqMessageClient.sendNotification(notificationLogId);

        return ResponseWrapper.success("消息入队成功！");
    }

    /**
     * @author youben
     * @date 2019年2月30日
     */
    @ResponseBody
    @RequestMapping(value = "/loadMessages", method = RequestMethod.POST)
    public ResponseWrapper loadMessages(String srcAppCode, String userName) {
        // 先查询缓存(不能影响正常业务逻辑)
        String notification_key = srcAppCode + "_" + userName;
        List<NotificationLog> notifications = cacheMessageClient.getNotification(notification_key);
        if (CollectionUtils.isNotEmpty(notifications)) {
            return ResponseWrapper.success(notifications);
        }
        // 缓存中没有命中，则去查询数据库
        List<NotificationLog> notificationLogs = notificationService.queryMessages(srcAppCode, userName);
        // 添加到缓存
        cacheMessageClient.cacheNotification(notification_key, notificationLogs);
        return ResponseWrapper.success(notificationLogs);
    }
}
