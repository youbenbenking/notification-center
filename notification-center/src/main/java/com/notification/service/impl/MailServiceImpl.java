package com.notification.service.impl;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.notification.service.MailService;
import com.notification.util.EnvUtil;

@Service
public class MailServiceImpl implements MailService{
	private static Logger logger=LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Autowired
	private JavaMailSender mailSender;
 
	@Value("${mail.fromMail.addr}")
	private String emailFrom;
	
	
	@Value("${email.to}")
	private String emailTo;
	@Value("${email.cc}")
	private String emailCc;
	
	/**
	 * @Description:发送简单邮件(收件人，主题，内容都暂时写死)
	 * @date:2018年3月16日
	 */
	@Override
	public void sendSimpleMail(String to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailFrom);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		try {
			mailSender.send(message);
		} catch (Exception e) {
			logger.error("发送简单邮件时发生异常！"+e);
		}
	}

	/**
	 * @Description:发送Html邮件(收件人，主题，内容都暂时写死)
	 * @date:2018年3月16日
	 */
	@Override
	public void sendHtmlMail(String to, String subject, String content) {
	    MimeMessage message = mailSender.createMimeMessage();
	    try {
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);   //true表示需要创建一个multipart message
	        helper.setFrom(emailFrom);
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(content, true);
	        mailSender.send(message);
	    } catch (MessagingException e) {
	    		logger.error("发送html邮件时发生异常！"+e);
	    }
	}

	/**
     * 发送带附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath){
        MimeMessage message = mailSender.createMimeMessage();
       
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            
            mailSender.send(message);
           
        } catch (MessagingException e) {
        		logger.error("发送带附件的邮件时发生异常！"+e);
       }
    }
    
    
    
    /**
     * 发送通知邮件通知到相关人员
     * 如果未配置邮件接收人员,会发送到youben，保证有技术人员知道异常
     * @param subject
     * @param content
     */
    @Override
    public void sendEmailNotice(String subject, String content) {
	        if (StringUtils.isEmpty(emailTo)) {
	            emailTo = new String("18271672815@163.com");
	        }
	        if (StringUtils.isEmpty(emailCc)) {
	            emailCc = new String("787438152@qq.com");
	        }
	        if (StringUtils.isEmpty(subject)) {
	            subject = new String("发送异常，未设置邮件标题");
	        }
	        if (StringUtils.isEmpty(content)) {
	        		content = new String("发送异常，邮件内容未设置具体异常信息，请检查异常");
	        }
	        String env = EnvUtil.getEnv();
	        subject = String.format("【%s】", env) + subject;
        
	        MimeMessage message = mailSender.createMimeMessage();
	        try {
	        		MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setFrom(emailFrom);
	            helper.setTo(emailTo);
	            helper.setCc(emailCc);
	            helper.setSubject(subject);
	            helper.setText(content, true);
	            
				mailSender.send(message);
				
	        } catch (MessagingException e) {
	        		logger.error("发送异常邮件时发生异常！"+e);
	       }
    }
    
    
}
