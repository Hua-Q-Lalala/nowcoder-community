package com.hua.community;

import com.hua.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * 过滤敏感字测试
 * @create 2022-03-29 15:58
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票，还有可以网上博彩";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以 赌  博，可以 嫖 娼，可以 吸 毒，可以开 票，还有可以网上 博   彩";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以赌abc博，可以嫖°°娼，可以吸°毒，可以开°票，还有可以网上博彩";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
