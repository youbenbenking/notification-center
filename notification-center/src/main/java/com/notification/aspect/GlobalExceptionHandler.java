package com.notification.aspect;

import javax.servlet.http.HttpServletRequest;

import com.notification.common.exception.ApiException;
import com.notification.common.result.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import com.notification.common.result.BaseResult;

/**
 * @author:youb
 * @date:2018/11/15
 * @desc:全局异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public BaseResult expHandler(HttpServletRequest request, Exception e) {
        try {
            String url = request.getRequestURI();
            String param = JSONObject.toJSONString(request.getParameterMap());
            if (StringUtils.contains(e.getMessage(), "断开的管道")) {
                log.warn("url={}, param={}, errorMsg={}", url, param, e.getMessage());
                return BaseResult.fail("系统开小差去了，请重试");
            } else {
                log.error("系统错误!url={}, param={}, errorMsg={}", url, param, e.getMessage());
                return BaseResult.fail("系统开小差去了，请重试");
            }
        } catch (Exception e2) {
            log.error("处理异常出错.", e2.getMessage(), e2);
            return BaseResult.fail("系统开小差去了，请重试");
        }
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseWrapper handleApiException(ApiException e) {
        log.error("GlobalExceptionHandler#handleApiException handle api exception code={}, message={}", e.getResultcode(), e.getMessage(), e);
        return ResponseWrapper.fail(e.getHttpstatus(), e.getResultcode(), e.getMessage());
    }
}
