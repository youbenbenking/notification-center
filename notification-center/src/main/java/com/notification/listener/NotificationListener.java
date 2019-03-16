package com.notification.listener;

import com.notification.constant.MQConstant;
import com.notification.model.NotificationMessage;

import org.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.*;


/**
 * 消息监听
 *
 */
@Component
public class NotificationListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate; 

    /**
     * 这里监听外部系统调用消息中心推送消息,服务端-->客户端的即时通知,所以处理要快,
     * 故将入库操作+清除消息通知缓存操作通过消息进一步异步处理
     * @param message
     */
    @RabbitListener(queues = MQConstant.MESSAGE_QUEUE)    
    public void listenCardQueue(NotificationMessage message) {
    		try {
    			String targetUsername = message.getDataEntity().getTargetUsername();
    			String msgDesc = message.getDataEntity().getMessage().getDesc();
    			
    			simpMessagingTemplate.convertAndSendToUser(targetUsername,"/message",msgDesc);
    			
            } catch (Exception e) {
                logger.error("监听队列[messageQueue]异常，请求参数:{}", message, e);
            }
    }
    
   
}
