package com.notification.listener;

import com.notification.constant.MQConstant;
import com.notification.dao.NotificationLogMapper;
import com.notification.model.NotificationLog;
import com.notification.redis.RedisUtil;

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
    
    @Autowired
	private NotificationLogMapper notificationLogMapper;
    

	@Autowired
	private RedisUtil redisUtil;

    /**
     * 这里监听外部系统调用消息中心推送消息,服务端-->客户端的即时通知,所以处理要快,
     * 故将入库操作+清除消息通知缓存操作通过消息进一步异步处理
     * @param message
     */
    @RabbitListener(queues = MQConstant.MESSAGE_QUEUE)    
    public void listenCardQueue(Long msgId) {
    		try {
    			//4.监听到队列消息,将消息状态置为[发送中]
    			notificationLogMapper.updateStatusByPrimaryKey(msgId,1);
    			
    			NotificationLog message = notificationLogMapper.selectByPrimaryKey(msgId);
    			String targetUserName = message.getTargetUserName();
    			String msgDesc = message.getMsgDesc();
    			
    			simpMessagingTemplate.convertAndSendToUser(targetUserName,"/message",msgDesc);
    			
    			//4.消息发送到客户端,将消息状态置为[发送成功]
    			notificationLogMapper.updateStatusByPrimaryKey(msgId,2);
    			try {
        			redisUtil.del(message.getSrcAppCode()+"_"+message.getTargetUserName());
        		} catch (Exception e) {
        			logger.error("消息通知删除Redis缓存异常!",e);
        		}
    			
            } catch (Exception e) {
                logger.error("监听队列[messageQueue]异常，请求参数:{}", msgId, e);
                
                //5.socket通信失败,将消息状态置为[发送失败]
                notificationLogMapper.updateStatusByPrimaryKey(msgId,3);
            }
    }
    
   
}
