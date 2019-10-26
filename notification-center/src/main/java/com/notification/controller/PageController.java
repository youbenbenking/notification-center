package com.notification.controller;

import javax.servlet.http.HttpServletRequest;

import com.notification.common.constant.LogTypeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j(topic = LogTypeConstants.WEB_LOGGER)
public class PageController {

	@Value("${notification.url}")
    private String notificationUrl;
	@Value("${notification.host}")
    private String notificationHost;
	/**
	 * 请求到登陆界面
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/")
    public String gotoLoginPage(HttpServletRequest request,ModelMap model){
		model.put("notificationUrl", notificationUrl);
		model.put("notificationHost", notificationHost);
		model.put("srcAppCode", "wisdom-platform");
		model.put("username", "youben");
		
		return "queue";
	}
}
