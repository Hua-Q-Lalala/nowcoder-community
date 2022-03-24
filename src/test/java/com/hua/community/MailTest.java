package com.hua.community;

import com.hua.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.security.auth.Subject;

/**
 * @create 2022-03-24 17:24
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testSendEmail(){
        mailClient.sendMail("1120269190@qq.com", "体制的", "this is test for email");


    }

    @Autowired
    private TemplateEngine templateEngine;  //(1.1)

    /**
     * 在（1.1）处注入了templates模板引擎，通过引擎对象传入html模板路径, 返回一个html内容的String对象，这里是使用Junit进行测试，
     * 所以在（2.1）处声明一个org.thymeleaf.context.Context对象，利用此对象向thymeleaf传参。
     * （2.3）调用方法生成动态模板，返回值是一个String对象。
     * （2.4）发送邮件
     */
    @Test
    public void testHtmlMail(){
        Context context = new Context(); //(2.1)
        context.setVariable("username", "李祥华，你已成功入职xxx，请于xx日来我司报告！<a href=\"https://www.bilibili.com/\">公司</a>");	//(2.2)

        String content = templateEngine.process("/mail/demo", context);	//(2.3)
        mailClient.sendMail("1120269190@qq.com", "Offer通知", content);	//(2.4)

        System.out.println(content);
    }
}
