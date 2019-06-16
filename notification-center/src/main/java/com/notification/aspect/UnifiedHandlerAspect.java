package com.notification.aspect;


import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.notification.enums.ResultCode;
import com.notification.response.ResponseWrapper;

/**
 * @author:youb
 * @date:2018/11/15
 * @desc:controller层统一异常处理，主要涉及两块：
 * 	1。service层校验或其他异常时直接抛出异常.(减少外层额外判断冗余，使代码简洁)
 * 	2。controller层try。。。catch抛出异常捕获。（抛出BadRequestException异常即可）
 */
@Component
@Aspect
@Profile("!test")
public class UnifiedHandlerAspect {
	
	private static final Log logger = LogFactory.getLog(UnifiedHandlerAspect.class);
   
	 @Around(value = "execution( * com.notification.controller..*.*(..))")
	    public Object around(ProceedingJoinPoint pjp) throws Throwable {
	        Object obj = null;
	        Class<?> returnType = null;
	        Signature signature = null;
	        boolean exceuteFlag = true;
	        String exceptionMessage = null;
	        long startTime = System.currentTimeMillis();
	        try {
	        	signature = pjp.getSignature();
	        	returnType = ((MethodSignature)signature).getReturnType();
	        	
	            obj = pjp.proceed();
	            return obj;
	        }  catch (Throwable e) {
	        	// 无权限错误不打印error日志
	        	if (StringUtils.contains(e.getMessage(), "无权限")) {
					logger.warn(e.getMessage(), e);
				}else {
					logger.error(e.getMessage(), e);
				}
	        	
	        	exceuteFlag = false;
	        	
	        	StringBuilder sb = new StringBuilder();
	        	sb.append(Objects.isNull(e.getMessage()) ? "内部系统错误" : e.getMessage());
	        	if (ResponseWrapper.class.isAssignableFrom(returnType)) {
					return ResponseWrapper.fail(ResultCode.SYSTEM_ERROR, e.getMessage());
				}else {
					// 这里进行其他类型判断
					return null;
				}
	        
	        }finally {
				// 这里进行日志打印
			}
	        
	    }
}
