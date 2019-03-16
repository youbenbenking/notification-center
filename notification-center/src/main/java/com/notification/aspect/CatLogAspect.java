package com.notification.aspect;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.notification.aop.CatTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * @author:binlongwang
 * @date:2018/5/1
 * @desc:增加切面 记录服务入参、出参日志
 */
@Service
@Aspect
public class CatLogAspect {
    private static final Log logger = LogFactory.getLog(CatLogAspect.class);

    /**
     * 增加切面 ,记录日志
     *
     * @param point
     */
    @Around(value = "@annotation(com.notification.aop.CatTransaction)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object value = null;
        String tilte = "CatLogAspect#around异常";
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class<?> clazz = target.getClass();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        Method method = clazz.getMethod(methodName, parameterTypes);
        CatTransaction data = method.getAnnotation(CatTransaction.class);
        //JOB都是一些后台线程，在Job开始加入一个埋点
        Transaction t = Cat.getProducer().newTransaction(data.projectName(), data.title());
        try {
            /*********执行方法**********/
            value = point.proceed();
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            logger.error(tilte, e);
            throw new Exception(e);
        } catch (Throwable throwable) {
            t.setStatus(throwable);
            logger.error(tilte, throwable);
            throw new Throwable(throwable);
        } finally {
            t.complete();
        }

        return value;
    }
}

