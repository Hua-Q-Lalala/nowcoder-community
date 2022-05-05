package com.hua.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AOP切面类，用于为service层记录日志
 * @create 2022-04-04 22:38
 */
@Component
@Aspect
public class ServiceLogAspect {

    /**
     * 日志类对象
     */
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 切点
     */
    @Pointcut("execution(* com.hua.community.service.*.*(..))")
    public void pointcut(){
    }

    /**
     * 前置方法，登记用户访问记录
     * @param joinPoint
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //日志格式：用户[ip地址], 在[时间]，访问了[com.hua.community.service.类名.方法（）]
        //1.获取request对象，从request对象中获取访问的ip地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        //获取当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取被调用的方法
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        //记录日志
        logger.info(String.format("用户[%s], 在[%s], 访问了[%s].", ip, now, target));
    }




















}
