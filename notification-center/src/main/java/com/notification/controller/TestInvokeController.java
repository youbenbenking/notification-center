package com.notification.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.notification.model.DataEntity;
import com.notification.model.MessageEntity;
import com.notification.model.NotificationMessage;
import com.notification.model.ReturnMessage;
import com.notification.util.HttpClientUtil;





@RestController
@RequestMapping("/api/v1")
public class TestInvokeController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	 
	 @Value("${notification_message_server_url}")
	 private String notificationMessageServerUrl;
    
    /**
	  * @author:youben
	  * @date:2019/3/21
	  * @desc: 测试通过http请求调用消息通知服务(这里模拟外部系统接口调用)
	  */
   @ResponseBody
   @RequestMapping(value = "/testInvokeNotification", method = RequestMethod.GET)
   public Object testRequestRest() {
	   ReturnMessage returnMessage = new ReturnMessage();
	   returnMessage.setSuccess(true);
	   returnMessage.setMsg("通知发送成功!");
	   
	   //拼装请求参数
       MessageEntity msg = new MessageEntity();
       msg.setTitle("智慧平台信息");
       msg.setDesc("智慧平台：现在收到新的通知消息了，快看看吧～");
       
       DataEntity dataEntity = new DataEntity() ;
       dataEntity.setMessage(msg);
       dataEntity.setTargetUsername("youben");
       
       NotificationMessage message = new NotificationMessage();
       message.setDataEntity(dataEntity);
       message.setSrcAppCode("wisdom-platform");
	   
	   //这里token改为"wisdom_wisdom-platform_youben" 加密生成
	   //json组装
	   JSONObject jsonObject = new JSONObject();
	   jsonObject.put("token", "7ec864cfa06538146515ff2f21824f60");
	   jsonObject.put("param", message);
	   
	   try {
		   String result = HttpClientUtil.httpPost(notificationMessageServerUrl, jsonObject.toJSONString());
			JSONObject resultJSONObject = JSONObject.parseObject(result);
			Boolean success = resultJSONObject.getBoolean("success");
			if (success == null || !success) {
				returnMessage.setSuccess(false);
				returnMessage.setMsg("通知发送失败!");
			}
	   	} catch (Exception e) {
	   		logger.error("调用消息通知服务异常!", e);
	   		returnMessage.setSuccess(false);
	   		returnMessage.setMsg("通知发送失败!");
		}
	   
		return returnMessage;
   }
   
}
