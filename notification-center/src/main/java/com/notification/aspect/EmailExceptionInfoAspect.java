package com.notification.aspect;

import com.google.gson.Gson;
import com.notification.service.MailService;
import com.notification.util.AspectJUtil;

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
public class EmailExceptionInfoAspect {
    private static final Log logger = LogFactory.getLog(EmailExceptionInfoAspect.class);

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
            Gson gson = new Gson();
            String parms = gson.toJson(pjp.getArgs());
            String title =String.format("执行【%s】异常", AspectJUtil.getMethod(pjp));
            String body= String.format("请求参数:【%s】,异常原因:【%s】", parms, e.toString());
            
            mailService.sendEmailNotice(title,body);
            
            logger.error("allexception around exception1", new Exception(body));
        } catch (Exception ex) {
            logger.error("allexception around exception2", ex);
        }
    }
}

