package com.notification.aspect;

import com.alibaba.fastjson.JSON;

import com.google.gson.Gson;
import com.notification.common.constant.LogTypeConstants;
import com.notification.service.MailService;
import com.notification.common.util.AspectJUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author:youb
 * @date:2019/3/10
 * @desc:增加切面 记录服务入参、出参日志
 */
@Service
@Aspect
@Slf4j
public class EmailExceptionInfoAspect {

    @Autowired
	private MailService mailService;

    @Around(value = "execution( * com.notification.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object obj = null;
        try {
            obj = pjp.proceed();
        }  catch (Exception e) {
            sendMsg(pjp, e);
            throw new Exception(e);
        }

        return obj;
    }

    /**
     * 异常信息通过邮件发送
     *
     * @param pjp
     * @param e
     */
    private void sendMsg(ProceedingJoinPoint pjp, Exception e) {
        try {
            String title =String.format("执行【%s】异常", AspectJUtil.getMethod(pjp));
            String body= String.format("请求参数:【%s】,异常原因:【%s】", JSON.toJSONString(pjp.getArgs()), e.toString());
            
            mailService.sendEmailNotice(title,body);
        } catch (Exception ex) {
            log.error("EmailExceptionInfoAspect#sendMsg error", ex.getMessage(), ex);
        }
    }
}

