package com.hua.community.controller;

import com.google.code.kaptcha.Producer;
import com.hua.community.entity.User;
import com.hua.community.service.UserService;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.CommunityUtil;
import com.hua.community.util.MailClient;
import com.hua.community.util.RedisKeyUtil;
import com.sun.media.sound.SoftTuning;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-03-24 22:53
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    //生成验证码的类
    @Autowired
    private Producer kaptchaProducer;

    //项目上下文路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 返回注册页面
     *
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 返回登录页面
     *
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        //Integer.parseInt("a");
        return "/site/login";
    }

    /**
     * 返回忘记密码页面
     *
     * @return
     */
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage() {
        return "/site/forget.html";
    }

    /**
     * 注册用户映射
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {  //map为空证明注册成功

            model.addAttribute("msg", "注册成功，我们已向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            //注册失败，从map中获取错误信息，返回到注册页面，显示错误信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活映射，需要参数userId 和 code
     * 激活链接 ： http://localhost:8080/community/activation/userId/code
     *
     * @param model
     * @param userId 用户id
     * @param code   激活码
     * @return
     */
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        //根据激活状态，返回相应操作
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    /**
     * (第一版，验证码存在session中)
     * 获取验证码，用于登录页面上使用
     * 使用Response对象返回图片验证码
     * 并使用Session对象将验证码保存在服务器
     *
     * @param response
     * @param session
     */
    /*
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText(); //将字符串保存到服务器，用于跟用户输入的验证码进行对比验证
        BufferedImage image = kaptchaProducer.createImage(text);    //通过验证码字符串生成图片，用于显示给用户看

        //将验证码存入到Session中，并向浏览器发送一个名为 JSESSIONID的Cookie
        session.setAttribute("kaptcha", text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }
    */

    /**
     * （第二版，验证码保存到redis中）
     * 获取验证码，用于登录页面上使用
     * <p>
     * 使用Response对象返回图片验证码
     *
     * @param response
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        //生成验证码
        String text = kaptchaProducer.createText(); //将字符串保存到服务器，用于跟用户输入的验证码进行对比验证
        BufferedImage image = kaptchaProducer.createImage(text);    //通过验证码字符串生成图片，用于显示给用户看

        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        //设置cookie有效时间为60秒
        cookie.setMaxAge(60);
        //设置cookie有效路径
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入Redis，过期时间设置为60秒
        String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /**
     * （第一版，验证码从session中获取）
     * 点击<立即登录>后处理登录
     *
     * @param username
     * @param password
     * @param code       验证码
     * @param rememberme //登录页面上 \<记住我>按钮的选项 true为勾选 false为不勾选
     * @param model
     * @param session    //用于获取服务器方生成的验证码
     * @param response   //用于向客户端返回一个名为ticket（登录凭证） 的cookie
     * @return
     */
    /*
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, HttpSession session, HttpServletResponse response) {
        String kaptcha = (String) session.getAttribute("kaptcha");
        //如果服务器保存的验证码为空 或者用户输入的验证码为空 又或者服务器保存的验证码与用户输入的验证码不相等，则返回验证码错误信息
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        //检查账号， 密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);    //设置此cookie有效的路径
            cookie.setMaxAge(expiredSeconds);   //设置此cookie有效时间
            response.addCookie(cookie);
            return "redirect:/index";   //登录成功，重定向到首页
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";   //登录失败，返回登录页面
        }
    }
    */


    /**
     * （第二版 验证码从Redis中获取）
     * 点击<立即登录>后处理登录
     *
     * @param username
     * @param password
     * @param code       验证码
     * @param rememberme //登录页面上 \<记住我>按钮的选项 true为勾选 false为不勾选
     * @param model
     * @param session    //用于获取服务器方生成的验证码
     * @param response   //用于向客户端返回一个名为ticket（登录凭证） 的cookie
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, HttpSession session, HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner) {
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            //从redis中获取验证码
            String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        //如果服务器保存的验证码为空 或者用户输入的验证码为空 又或者服务器保存的验证码与用户输入的验证码不相等，则返回验证码错误信息
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        //检查账号， 密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);    //设置此cookie有效的路径
            cookie.setMaxAge(expiredSeconds);   //设置此cookie有效时间
            response.addCookie(cookie);
            return "redirect:/index";   //登录成功，重定向到首页
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";   //登录失败，返回登录页面
        }
    }

    /**
     * 退出登录
     * @param ticket
     * @param request
     * @return
     */
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket, HttpServletRequest request) {
        //将ticket设置为无效
        userService.logout(ticket);

        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        for (Cookie cookie1 : cookies) {
            if (cookie1.getName().equals("ticket")) {
                cookie = cookie1;
            }
            System.out.println(cookie1.getName() + "  " + cookie1.getMaxAge());
        }

        return "redirect:/login";
    }

    /**
     * 获取验证码，用于忘记密码功能
     *
     * @param email
     * @param session 利用Session对象将验证码保存在服务端
     * @return 返回json数据，如果map为空说明邮件已经发送成功
     */
    @RequestMapping(path = "/forget/code/{email}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getForgetKaptcha(@PathVariable("email") String email, HttpSession session) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //通过邮箱查找用户是否存在
        User user = userService.findUser(email);
        if (user == null) {
            map.put("emailMsg", "用户未注册");
            return map;
        }

        System.out.println("forgetKaptcha");

        //生成验证码
        String text = kaptchaProducer.createText();

        //将验证码保存到session中，以eamil为key
        session.setAttribute(email + "forgetKaptcha", text);   //利用email绑定key 保证获取验证码的账号 和 提交更改时的email保持一致

        Context context = new Context();
        context.setVariable("forgetKaptcha", text);
        context.setVariable("forgetEmail", email);
        //生成动态模板
        String content = templateEngine.process("/mail/forget", context);

        //发送邮件
        mailClient.sendMail(email, "牛客社区网 找回密码", content);

        return map;
    }

    /**
     * 点击<重置密码>后处理修改密码
     *
     * @param email
     * @param code
     * @param newPassword
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(path = "/forget/password", method = RequestMethod.POST)
    public String reset(String email, String code, String newPassword, Model model, HttpSession session) {
        //利用email为key从session中获取验证码
        //如果获得的验证码为空，说明未给此邮箱发送过验证码
        String kaptcha = (String) session.getAttribute(email + "forgetKaptcha");
        //判断验证码是否相同
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/forget";
        }

        //判断新密码是否为空
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("passwordMsg", "新密码不能为空");
            return "/site/forget";
        }

        //新密码去除首尾空格 修改密码，修改成功后跳到提示页面，并跳转到登录页面
        int rows = userService.resetPassword(email, newPassword.trim());
        model.addAttribute("msg", "您的密码已经修改成功！");
        model.addAttribute("target", "/login");

        //跳到提示页面
        return "/site/operate-result";
    }


}
