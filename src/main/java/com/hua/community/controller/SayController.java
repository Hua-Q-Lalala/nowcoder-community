package com.hua.community.controller;

import com.hua.community.service.AlphaService;
import com.hua.community.util.CommunityUtil;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 代码示例， 与项目业务方面不相干
 * @create 2022-03-21 18:27
 */
@Controller
@RequestMapping("/h")
public class SayController {

    @Autowired
    private AlphaService alphaService;

    //path 和 value在@RequestMapping注解上一个意思，都是代表请求路径
    @RequestMapping(value = {"/say", "speak"})
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/getData")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }


        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html; charset=utf-8");
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            printWriter.close();
        }

    }

    //GET请求

    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //RestFull风格
    @RequestMapping(path = "/student/{id}/{username}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable int id, @PathVariable String username){
        System.out.println(id);
        System.out.println(username);
        return "a student";
    }

    //POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTTP数据
    //方式一：
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "猪八戒");
        modelAndView.addObject("age", 99);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    //方式二
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", "150");
        return "/demo/view";
    }

    //响应JSON格式数据 （常用于异步请求）
    //将java对象 转 JSON字符串 再返回给浏览器
    @RequestMapping(path = "emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", "张三");
        map.put("age", 19);
        map.put("salary", 9000);

        return map;
    }

    @RequestMapping(path = "emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", "张三");
        map.put("age", 19);
        map.put("salary", 9000);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "李四");
        map.put("age", 20);
        map.put("salary", 7000);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "王五");
        map.put("age", 24);
        map.put("salary", 12000);
        list.add(map);

        return list;
    }


    //cookie示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookit(HttpServletResponse response){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效的范围
        cookie.setPath("/community/h");
        //设置cookie的生存时间 单位秒
        cookie.setMaxAge(60 * 10);
        //发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", 1);
        session.setAttribute("name", "test");
        return "set session";
    }

    //session示例
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        System.out.println(session.getId());
        return "set session";
    }

    //ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        //0代表成功，msg为提示信息
        return CommunityUtil.getJsonString(0, "操作成功");
    }

    @RequestMapping(path = "/getContextPath", method = RequestMethod.GET)
    public void getContextPath(HttpServletRequest request){

        ServletContext context = request.getServletContext();
        String realPath =context.getRealPath("discuss-detail.css");
        System.out.println(realPath + "路径");

        
    }

}
