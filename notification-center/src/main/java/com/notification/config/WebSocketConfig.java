package com.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * 注册websocket
 */
@Configuration
@EnableWebSocketMessageBroker  //注解开启STOMP协议来传输基于代理的消息，此时控制器支持使用
@MessageMapping
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	 /**
     * 配置了一个简单的消息代理
     * 消息代理将会处理前缀为"/topic"和"/queue"的消息。
     * @param registry
     */
	@Override  
    public void configureMessageBroker(MessageBrokerRegistry config) {  
        config.enableSimpleBroker("/topic","/queue");//topic用来广播，user用来实现p2p
        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
        config.setApplicationDestinationPrefixes("/app");
    }  
  
    @Override  
    public void registerStompEndpoints(StompEndpointRegistry registry) {  
    		//WebSocket的配置也需要支持跨域
        registry.addEndpoint("/webServer").setAllowedOrigins("*").withSockJS();  
        registry.addEndpoint("/queueServer").setAllowedOrigins("*").withSockJS();//注册两个STOMP的endpoint，分别用于广播和点对点  
    }  

}
