package com.hua.community.config;

import com.hua.community.annotation.LoginRequired;
import com.hua.community.controller.interceptor.AlphaInterceptor;
import com.hua.community.controller.interceptor.LoginRequiredInterceptor;
import com.hua.community.controller.interceptor.LoginTicketInterceptor;
import com.hua.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 * @create 2022-03-26 16:58
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*jpeg")
                        .addPathPatterns("/register", "/login");    //示例，与项目无关联

        registry.addInterceptor(loginTicketInterceptor)   //添加拦截器
                        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*jpeg");

        registry.addInterceptor(loginRequiredInterceptor)   //添加拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*jpeg");

        registry.addInterceptor(messageInterceptor)   //添加拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*jpeg");
    }






















}
