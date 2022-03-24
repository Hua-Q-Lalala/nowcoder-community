package com.hua.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @create 2022-03-23 0:43
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class LoggerTest {

    //从LoggerFactory中获取一个明为LoggerTest.class全限定名的logger对象
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        //调用debug方法 打印debug级别日志
        logger.debug("this is debug level message");
        //调用info方法 打印info级别日志
        logger.info("this is info level message");
        //调用warn方法 打印warn级别日志
        logger.warn("this is warn level message");
        //调用error方法 打印error级别日志
        logger.error("this is error level message");
    }
}
