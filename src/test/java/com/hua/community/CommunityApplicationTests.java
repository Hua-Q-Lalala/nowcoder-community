package com.hua.community;

import com.hua.community.dao.AlphaDao;
import com.hua.community.service.AlphaService;
import org.aspectj.weaver.ast.Var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取Spring容器步骤：
 * 1.实现ApplicationContextAware重新setApplicationContext方法
 * 2.生明成员变量ApplicationContext applicationContext
 * 3.在setApplicationContext方法中将形参赋值给成员变量ApplicationContext applicationContext
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //测试按类型获取bean和按id获取bean
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);

        AlphaDao bean = applicationContext.getBean(AlphaDao.class); //按类型获取bean
        System.out.println(bean.select());

        AlphaDao hibernate = (AlphaDao) applicationContext.getBean("alphaDaoHibernate");    //按id获取bean
        System.out.println(hibernate.select());

    }

    //测试init()方法和destroy()方法
    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        alphaService = applicationContext.getBean(AlphaService.class);
    }

    //测试自定义配置类
    @Test
    public void testBeanConfig(){
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    //@Autowired
    //@Qualifier("alphaDaoHibernate")     //按id进行注入
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    @Qualifier("alphaDaoHibernate")
    public void setAlphaDao(AlphaDao alphaDao){
        this.alphaDao = alphaDao;
    }

    //测试使用autuwired自动注入bean
    @Test
    public void testAutuwriedBean(){
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
    }

    @Test
    public void testDate(){
        System.out.println(new Date(0));
        System.out.println(new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss").format(new Date(0)));

    }
}
