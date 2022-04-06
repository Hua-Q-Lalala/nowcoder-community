package com.hua.community.controller.advice;

import com.hua.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 仅在发生异常是被调用
 * 异常通知类: 用于处理所有在Controller层上发生的异常
 * @create 2022-04-04 20:24
 */
@ControllerAdvice(annotations = Controller.class)   //限制只扫描带有controller注解的类
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());

        //将异常栈信息记录到日志
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //X-Requested-With: XMLHttpRequest
        String xRequestWith = request.getHeader("X-Requested-With");
        //判断是否为Ajax请求，如果是则返回一串Json格式的错误信息
        if ("XMLHttpRequest".equals(xRequestWith)){
            response.setContentType("application/plain; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1, "服务器异常"));
        } else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
