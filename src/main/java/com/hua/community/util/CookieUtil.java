package com.hua.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @create 2022-03-26 17:23
 */
public class CookieUtil {

    /**
     * 用于获取指定cookie值
     * @param request
     * @param name
     * @return
     */
    public static String getValue(HttpServletRequest request, String name){
        if (request == null || name == null){
            throw new IllegalArgumentException("参数为空");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
