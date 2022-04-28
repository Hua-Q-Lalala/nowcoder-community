package com.hua.community.controller.interceptor;

import com.hua.community.entity.LoginTicket;
import com.hua.community.entity.User;
import com.hua.community.service.UserService;
import com.hua.community.util.CookieUtil;
import com.hua.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 此拦截器用于从前端cookie中获取登录凭证，根据登录凭证从数据库中查询用户信息，
 * 并利用ThreadLocal将用户信息保存在当前的请求线程中
 * @create 2022-03-26 17:26
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //查询凭证是否有效
            // 仅当loginTicket不为空 并且 status等于0 并且 过期时间在当前时间之后，凭证才有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户 将user信息保存到当前线程中，只要当前线程存活，user一直存在
                hostHolder.setUser(user);
            }
        }
        //拦截器放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();   //获取当前线程所拥有的user对象
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();     //请求结束后，清除当前线程拥有的user对象
    }
}
