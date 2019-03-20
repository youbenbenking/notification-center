package com.notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.notification.service.MailService;
 
 
/**
 * @Description:发送邮件单元测试,Method should be void
 * @author youb
 * @date:2019年3月20日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootNotificationApplication.class)
public class EmailTest {
		@Autowired
		private MailService mailService;
		
		@Autowired
		private TemplateEngine templateEngine;
		
	
		//发送带有附件的邮件
		@Test
		public void sendAttachmentsMail() {
			String filePath="/Users/youben/Desktop/小笔记/测试部署.txt";
			mailService.sendAttachmentsMail("787438152@qq.com", "主题：带附件的邮件", "有附件，请查收！", filePath);
		}
		
		
		//发送模板邮件
		@Test
		public void sendTemplateMail() {
				//创建邮件正文,这里主要使用的 键值对 的形式，类似map，将模板里面的需要的变量与值对应起来。
				Context context = new Context();
				context.setVariable("user", "youb");
				context.setVariable("title", "测试公司");
				context.setVariable("description","消息中心");
				context.setVariable("status","消息中心");
				context.setVariable("createDate","消息中心");
				context.setVariable("leftTime","消息中心");
				context.setVariable("members","消息中心");
				String emailContent = templateEngine.process("emailTemplate", context);
				mailService.sendHtmlMail("787438152@qq.com","主题：这是模板邮件",emailContent);
		}
		
}
		
