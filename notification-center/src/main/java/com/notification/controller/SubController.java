package com.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.notification.model.ReceiveMessage;


@Controller
public class SubController {
	
	@Autowired
    public SimpMessagingTemplate template;  
      
    
    @MessageMapping("/subscribe")
    public void subscribe(ReceiveMessage rm) {
        for(int i =1;i<=20;i++) {
            //广播使用convertAndSend方法，第一个参数为目的地，和js中订阅的目的地要一致
            template.convertAndSend("/topic/getResponse", rm.getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    
    
    /**
     * 前端主动推消息
     * @param rm
     */
    @MessageMapping("/queue")
    public void queuw(ReceiveMessage rm) {
        System.out.println("创建连接!");
//        for(int i =1;i<=20;i++) {
//            /*广播使用convertAndSendToUser方法，第一个参数为用户id，此时js中的订阅地址为
//            "/user/" + 用户Id + "/message",其中"/user"是固定的*/
//            template.convertAndSendToUser("zhangsan","/message",rm.getName());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }
    
}
