package com.notification.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 * @author youben
 *
 */
@RestController
@RequestMapping("/api")
public class NotificationController {
	private static Logger logger=LoggerFactory.getLogger(NotificationController.class);
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	 private NotificationLogMapper notificationLogMapper;
	
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 30,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>());
	
   
    @RequestMapping(value = "/appUserMsg", method = RequestMethod.POST)
    public Object robotSerchResultCaseStop(@RequestBody RestfulParamVo<NotificationMessage> param) {
    		//验证参数信息
    		if(param == null || StringUtils.isEmpty(param.getToken())) {
    			new ReturnMessage(false, "参数信息错误！");
    		}
    		
    		//校验token
    		if(!param.getToken().equalsIgnoreCase("7ec864cfa06538146515ff2f21824f60") ) {
    			new ReturnMessage(false, "token验证失败！");
    		}
    		
    		NotificationMessage message = param.getParam();
        //发送消息队列,异步解耦
        amqpTemplate.convertAndSend(MQConstant.WISDOM_EXCHANGE, MQConstant.MESSAGE_QUEUE, message);
        
	 	//异步处理消息入库及清除消息通知缓存
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
	        			String srcAppCode = message.getSrcAppCode();
	        			String targetUsername = message.getDataEntity().getTargetUsername();
	        			
	            		NotificationLog notificationLog = new NotificationLog();
	            		notificationLog.setSrcAppCode(srcAppCode);
	            		notificationLog.setTargetUserName(targetUsername);
	            		notificationLog.setMsgTitle(message.getDataEntity().getMessage().getTitle());
	            		notificationLog.setMsgDesc(message.getDataEntity().getMessage().getDesc());
	            		notificationLog.setMsgLink(message.getDataEntity().getMessage().getLink());
	            		notificationLog.setMsgType(message.getDataEntity().getMessage().getType());
	            		notificationLog.setCreateTime(new Date());
	            		
	            		notificationLogMapper.insertSelective(notificationLog);
	        			
	            		try {
	            			redisUtil.del(srcAppCode+"_"+targetUsername);
	            		} catch (Exception e) {
	            			logger.error("消息通知删除Redis缓存异常!",e);
	            		}
	        			
        			
                } catch (Exception e) {
                    logger.error("异步处理消息入库及清除消息通知缓存异常，请求参数:{}", message, e);
                }
            }
        });
    		
        return new ReturnMessage(true, "消息入队成功！");
    }
    
    
    /**
     * @author youben
     * @date 2019年2月30日
     */
    @ResponseBody
    @RequestMapping(value = "/loadMessages", method = RequestMethod.POST)
    public Object loadMessages( String srcAppCode, String userName) {
        
        	ReturnMessage message = new ReturnMessage();
        	message.setSuccess(true);
        	message.setMsg("查询成功!");
        	
        	//添加缓存不能影响正常业务逻辑
    		//先查询缓存************************
        	String notification_key = srcAppCode+"_"+userName;
    		try {
    			String json=(String) redisUtil.get(notification_key);
    			if(StringUtils.isNotBlank(json)){
    				List<NotificationLog> notificationList=JsonUtil.jsonToList(json, NotificationLog.class);
    				message.setData(notificationList);
    				return message;
    			}
    		} catch (Exception e) {
    			logger.error("消息通知Redis获取缓存异常!",e);
    		}
    		
    		//缓存中没有命中，则去查询数据库
    			List<NotificationLog> notificationList = notificationLogMapper.selectMessages( srcAppCode, userName);
            if (CollectionUtils.isEmpty(notificationList)) {
            		notificationList = new ArrayList<>();
			}
            
    		//把结果添加到缓存************************
    		try {
    			redisUtil.set(notification_key, JsonUtil.objectToJson(notificationList),86400);
    		} catch (Exception e) {
    			logger.error("消息通知Redis缓存异常!",e);
    		}
    		
    		message.setData(notificationList);
    		return message;
    }
}
