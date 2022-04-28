package com.hua.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @create 2022-03-24 17:17
 */
@Component
public class MailClient {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;	//邮件发送器

    @Value("${spring.mail.username}")
    private String from;	//发送方邮箱

    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //设置发送方
            helper.setFrom(from);
            //接收方
            helper.setTo(to);
            //主题
            helper.setSubject(subject);
            //内容，并允许发送html格式的文件
            helper.setText(content, true);
            //发送
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }

    }
}
