package com.hua.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码生成配置类
 * @create 2022-03-26 0:02
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");   //宽度
        properties.setProperty("kaptcha.image.height", "40");   //高度
        properties.setProperty("kaptcha.textproducer.font.size", "32");     //字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");     //颜色
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLNMOPQRSTUVWXYZ");     //从串中随机生成验证码
        properties.setProperty("kaptcha.textproducer.char.length", "4");    //随机验证码的长度为4
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");   //不设置干扰线

        //具体的验证码生成器
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);

        return kaptcha;
    }
































}
