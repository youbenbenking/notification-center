package com.notification.aspect;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.notification.response.BaseResult;

/**
 * @author:youb
 * @date:2018/11/15
 * @desc:全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
	private static final Log logger = LogFactory.getLog(UnifiedHandlerAspect.class);
	
	@ExceptionHandler
	@ResponseBody
	public BaseResult expHandler(HttpServletRequest request, Exception e) {
		try {
			String url = request.getRequestURI();
			String param = JSONObject.toJSONString(request.getParameterMap());
			if (StringUtils.contains(e.getMessage(), "断开的管道")) {
				return BaseResult.fail("系统开小差去了，请重试");
			}else {
				return BaseResult.fail("系统开小差去了，请重试");
			}
		} catch (Exception e2) {
			return BaseResult.fail("系统开小差去了，请重试");
		}
	}
}
