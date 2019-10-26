package com.notification.common.util;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;


/**
 * @Title: AspectJUtil.java
 * @Description: 组装连接类+方法
 * @author youben
 */
public class AspectJUtil {
	private static final Log logger = LogFactory.getLog(AspectJUtil.class);

	public static String getMethod(ProceedingJoinPoint pjp) {
        if (null == pjp) {
            return "";
        }
        try {
            Signature sig = pjp.getSignature();
            MethodSignature msig = null;
            //获取实现类的方法，非接口的方法
            if (!(sig instanceof MethodSignature)) {
                return "";
            }
            msig = (MethodSignature) sig;
            Object target = pjp.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            return target.getClass() + "." + currentMethod.getName();
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return "";
    }
}
