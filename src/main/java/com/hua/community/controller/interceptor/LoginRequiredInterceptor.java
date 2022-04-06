package com.hua.community.controller.interceptor;

import com.hua.community.annotation.LoginRequired;
import com.hua.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 用于拦截一些需要进行登录才能访问的请求，如果没有登录则不给予放行
 * @create 2022-03-27 14:14
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {    //判断此请求的请求是不是一个方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;  //强制转型
            Method method = handlerMethod.getMethod();  //获得反射Method类对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);    //根据Method实列获取方法上的LoginRequired注解
            //如果方法标注有weLoginRequired注解 并且未登录，则重定向到登录页面，并终止此次请求
            if(loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }



        return true;
    }
















}
