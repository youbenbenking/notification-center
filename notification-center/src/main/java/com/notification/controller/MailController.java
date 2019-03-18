package com.notification.controller;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.notification.service.MailService;
 
 
/**
 * @Description:发送邮件的Controller
 * @author youb
 * @date:2019年3月16日
 */
@RestController
@RequestMapping("/mail")
public class MailController {
		@Autowired
		private MailService mailService;
		
		@Autowired
		private TemplateEngine templateEngine;
		
		/**
		 * 发送简单邮件(收件人，主题，内容都暂时写死)
		 */
		@RequestMapping("/sendSimpleMail")
		public String sendSimpleMail() {
			String to = "787438152@qq.com";
			String subject = "test simple mail";
			String content = "hello, this is simple mail";
			mailService.sendSimpleMail(to, subject, content);
			return "success";
		}
	
	
		/**
		 * 发送Html格式的邮件(收件人，主题，内容都暂时写死)
		 */
		@RequestMapping("/sendHtmlMail")
		public String  sendHtmlMail() {
			String to = "787438152@qq.com";
			String subject = "test html mail";
			String content = "hello, this is html mail";
			mailService.sendHtmlMail(to, subject, content);
			return "success";
		}

	
		/**
		 * 发送带有附件的邮件
		 */
		@Test
		@RequestMapping("/sendAttachmentsMail")
		public String sendAttachmentsMail() {
			String filePath="/Users/youben/Desktop/小笔记/测试部署.txt";
			mailService.sendAttachmentsMail("787438152@qq.com", "主题：带附件的邮件", "有附件，请查收！", filePath);
			return "success";
		}
		
		
		/**
		 *发送模板邮件
		 */
		@Test
		@RequestMapping("/sendTemplateMail")
		public String sendTemplateMail() {
			//创建邮件正文
				Context context = new Context();
				context.setVariable("user", "youb");
				context.setVariable("web", "智慧管理");
				context.setVariable("company", "测试公司");
				context.setVariable("product","消息中心");
				String emailContent = templateEngine.process("emailTemplate", context);
				mailService.sendHtmlMail("787438152@qq.com","主题：这是模板邮件",emailContent);
				return "sucess";
			}
}
		
