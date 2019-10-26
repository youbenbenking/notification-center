package com.notification.integration;

import com.notification.constant.LogTypeConstants;
import com.notification.constant.MQConstant;
import com.notification.enums.ResultCode;
import com.notification.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = LogTypeConstants.INTEGRATION_LOGGER)
public class MQMessageClient {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendNotification(Long notificationLogId){
        try {
            amqpTemplate.convertAndSend(MQConstant.WISDOM_EXCHANGE, MQConstant.MESSAGE_QUEUE, notificationLogId);
        } catch (Exception e) {
            log.error("NotificationController#receive error notificationLogId={}.",notificationLogId, e);
            throw new BadRequestException(ResultCode.MSG_ENTER_QUEUE_FAILED);
        }
    }
}
