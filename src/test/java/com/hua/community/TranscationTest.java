package com.hua.community;

import com.hua.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @create 2022-03-31 17:07
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class TranscationTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object o = alphaService.save1();
        System.out.println(o);
    }

    @Test
    public void testSave2(){
        Object o = alphaService.save2();
        System.out.println(o);
    }

    @Test
    public void test(){
        new Animal(){
            @Override
            public void say() {
                System.out.println("匿名子类");
            }
        }.say();
    }

    class Animal{
        public void say(){
            System.out.println("父类");
        }
    }

}
