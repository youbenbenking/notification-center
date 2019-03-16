package com.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * redis配置类
 *SpringBoot自动在容器中生成了一个RedisTemplate,但是它的泛型是<Object,Object>，使用过程需要各种类型转换,
 *因此需要一个泛型为<String,Object>形式的RedisTemplate。
 *redis自动配置类中有@ConditionalOnMissingBean注解后，说明如果Spring容器中有了RedisTemplate对象了，这个自动配置的RedisTemplate不会实例化。
 *因此自定义一个配置类
 * @author youb 2019-1-02 20:48:20
 */
@Configuration
public class RedisConfig  {
    
		@Bean
	    @SuppressWarnings("all")
	    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
	        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	        template.setConnectionFactory(factory);
	        
	        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
	        ObjectMapper om = new ObjectMapper();
	        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	        jackson2JsonRedisSerializer.setObjectMapper(om);
	        
	        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
	        // key采用String的序列化方式
	        template.setKeySerializer(stringRedisSerializer);
	        // hash的key也采用String的序列化方式
	        template.setHashKeySerializer(stringRedisSerializer);
	        // value序列化方式采用jackson
	        template.setValueSerializer(jackson2JsonRedisSerializer);
	        // hash的value序列化方式采用jackson
	        template.setHashValueSerializer(jackson2JsonRedisSerializer);
	        template.afterPropertiesSet();
	        return template;
	    }

}