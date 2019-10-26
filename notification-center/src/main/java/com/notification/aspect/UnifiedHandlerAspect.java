package com.notification.aspect;

import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.notification.common.constant.LogTypeConstants;
import com.notification.common.enums.ResultCode;
import com.notification.common.exception.ApiException;
import com.notification.common.result.BaseResult;
import com.notification.common.result.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author:youb
 * @date:2018/11/15
 * @desc:controller层统一异常处理，主要涉及两块：
 * 	1。service层校验或其他异常时直接抛出异常.(减少外层额外判断冗余，使代码简洁)
 * 	2。controller层try。。。catch抛出异常捕获。（抛出BadRequestException异常即可）
 */
@Component
@Aspect
@Slf4j(topic = LogTypeConstants.WEB_LOGGER)
@Profile("!test")
public class UnifiedHandlerAspect {
   
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
	        	// 这里可以设定什么样类型异常不打印错误日志，如：无权限
	        	log.error(e.getMessage(), e);

	        	exceuteFlag = false;
				exceptionMessage = e.getMessage();

	        	StringBuilder str = new StringBuilder();
	        	str.append(Objects.isNull(e.getMessage()) ? "内部系统错误" : e.getMessage());
	        	if (ResponseWrapper.class.isAssignableFrom(returnType)) {
					final ResultCode resultCode = e instanceof ApiException ? ((ApiException)e).getResultcode() : ResultCode.SYSTEM_ERROR;
					return ResponseWrapper.fail(resultCode, e.getMessage());
				}else{
					return BaseResult.fail(str.toString());
				}
	        }finally {
	        	long executeTime = System.currentTimeMillis() - startTime;
	        	if (exceuteFlag){
	        		log.info("{}|{}|{}|{}|{}", signature.getDeclaringTypeName() + "#" +signature.getName(), JSON.toJSONString(pjp.getArgs()),
						executeTime, exceuteFlag, JSON.toJSONString(obj));
				}else {
	        		log.error("{}|{}|{}|{}|{}", signature.getDeclaringTypeName() + "#" +signature.getName(), JSON.toJSONString(pjp.getArgs()),
						executeTime, exceuteFlag, exceptionMessage);
				}
				MDC.clear();
			}
	    }
}
