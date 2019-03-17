package com.notification.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PageController {
	private static Logger logger=LoggerFactory.getLogger(PageController.class);
	
	@Value("${notification.url}")
    private String notificationUrl;
	@Value("${notification.host}")
    private String notificationHost;
	/**
	 * 请求到登陆界面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/",method = RequestMethod.GET)
    public String gotoLoginPage(HttpServletRequest request,ModelMap model) throws Exception{
		model.put("notificationUrl", notificationUrl);
		model.put("notificationHost", notificationHost);
		model.put("srcAppCode", "wisdom-platform");
		model.put("username", "youben");
		
		return "queue";
	}
}
