package com.notification.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.notification.convert.NotificationConvertor;
import com.notification.enums.ResultCode;
import com.notification.exception.BadRequestException;
import com.notification.response.ResponseWrapper;
import com.notification.service.NotificationService;
import com.notification.validate.NotificationValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.notification.constant.MQConstant;
import com.notification.dao.NotificationLogMapper;
import com.notification.model.NotificationLog;
import com.notification.model.NotificationMessage;
import com.notification.model.ReturnMessage;
import com.notification.redis.RedisUtil;
import com.notification.util.JsonUtil;
import com.notification.vo.RestfulParamVo;

/**
 * 消息通知处理类
 *
 * @author youben
 */
@RestController
@RequestMapping("/api")
public class NotificationController {
    private static Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationConvertor notificationConvertor;

    @Autowired
    private NotificationValidator notificationValidator;

    @Autowired
    private RedisUtil redisUtil;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 30,
        TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    @RequestMapping(value = "/appUserMsg", method = RequestMethod.POST)
    public ResponseWrapper receive(@RequestBody RestfulParamVo<NotificationMessage> param) {
        notificationValidator.validateReceive(param);
        //消息入库,默认[待发送]
        NotificationLog notificationLog = notificationService.save(notificationConvertor.convertToDto(param));
        //发送消息队列,异步解耦,提高并发(即使入队失败不应该影响消息入库)
        try {
            amqpTemplate.convertAndSend(MQConstant.WISDOM_EXCHANGE, MQConstant.MESSAGE_QUEUE, notificationLog.getId());
        } catch (Exception e) {
            logger.error("消息通知入队异常！", e);
            throw new BadRequestException(ResultCode.MSG_ENTER_QUEUE_FIALD);
        }
        return ResponseWrapper.success("消息入队成功！");
    }

    /**
     * @author youben
     * @date 2019年2月30日
     */
    @ResponseBody
    @RequestMapping(value = "/loadMessages", method = RequestMethod.POST)
    public ResponseWrapper loadMessages(String srcAppCode, String userName) {

        //添加缓存不能影响正常业务逻辑
        //先查询缓存************************
        String notification_key = srcAppCode + "_" + userName;
        try {
            String json = (String)redisUtil.get(notification_key);
            if (StringUtils.isNotBlank(json)) {
                List<NotificationLog> notificationList = JsonUtil.jsonToList(json, NotificationLog.class);
                return ResponseWrapper.success(notificationList);
            }
        } catch (Exception e) {
            logger.error("消息通知Redis获取缓存异常!", e);
            throw new BadRequestException(ResultCode.QUERY_CHANE_MSG_FIALD);
        }

        //缓存中没有命中，则去查询数据库
        List<NotificationLog> notificationLogs = notificationService.queryMessages(srcAppCode, userName);

        //把结果添加到缓存************************
            boolean flag = redisUtil.set(notification_key, JsonUtil.objectToJson(notificationLogs), 86400);
            if (!flag) {
                throw new BadRequestException(ResultCode.SET_CHANE_MSG_FIALD);
            }
        return ResponseWrapper.success(notificationLogs);
    }
}
