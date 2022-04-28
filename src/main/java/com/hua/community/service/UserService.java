package com.hua.community.service;

import com.hua.community.dao.LoginTicketMapper;
import com.hua.community.dao.UserMapper;
import com.hua.community.entity.LoginTicket;
import com.hua.community.entity.User;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.CommunityUtil;
import com.hua.community.util.MailClient;
import com.hua.community.util.RedisKeyUtil;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-03-22 14:53
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;  //邮件发送

    @Autowired
    private TemplateEngine templateEngine;  //thymeleaf模板引擎

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据userId获取用户
     * @param userId
     * @return
     */
    public User findUserById(int userId) {
        /*（第一版，直接中mysql中查询）
        return userMapper.selectById(userId);
         */

        //第二版，先从redis中查询，如果没有再去mysql中查询
        User user = getCache(userId);       //从redis中获取
        if(user == null){
            user = initCache(userId);       //从mysql中获取，并将结果保存到redis中
        }

        return user;
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空。");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在。");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5)); //获取长度为5的salt字符串
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);    //用户类型
        user.setStatus(0);  //用户状态 0未激活 1已激活
        user.setActivationCode(CommunityUtil.generateUUID());   //随机激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));    //设置随机头像
        user.setCreateTime(new Date());

        //将user信息插入到数据库
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //激活链接 ： http://localhost:8080/community/activation/userId/code
        //生成激活链接
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        //根据context生成动态html模板
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活用户
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);  //根据用户id返回user对象
        System.out.println(user);
        if (user.getStatus() == 1){ //如果状态码已经为1，说明重复激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)){  //如果激活码相同，则激活
            userMapper.updateStatus(userId, 1);
            clearCache(userId); //数据发生更新，清除缓存
            return ACTIVATION_SUCCESS;
        } else{ //否则失败
            return ACTIVATION_FAILUER;
        }

    }

    /**
     * 登录
     * @param username  用户名
     * @param password  //密码
     * @param expiredSeconds    //过期时间，单位秒
     * @return
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "账号不存在");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){  //密码不正确
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());    //利用UUID生成不重复的字符串
        loginTicket.setStatus(0);   //设置登录凭证是否有效 0：有效；1：无效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        /*
        （ticket第一版，将ticket保存到mysql数据库）
        loginTicketMapper.insertLoginTicket(loginTicket);
         */

        //（ticket第二版，将ticket保存到Redis数据库）
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //redis中保存的是loginTicket序列化后的json字符串
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 登出
     * @param ticket
     */
    public void logout(String ticket){
        /*(ticket第一版， 修改在mysql数据库中的ticket状态)
        loginTicketMapper.updateStatus(ticket, 1);
         */

        //（ticket第二版，修改redis中保存的ticket状态）
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        //redis中保存的是loginTicket序列化后的json字符串
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    /**
     * 根据ticket返回一个LoginTicket对象
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket){
        /* (ticket第一版， 从mysql数据库中获取ticet信息)
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
         */

        //(ticket第二版， 从redis数据库中获取ticet信息)
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    /**
     * 更新用户头像
     * @param userId    用户id
     * @param headerUrl     新的头像地址链接
     * @return
     */
    public int updateHeaderUrl(int userId, String headerUrl){
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId); //数据发生更新， 清除缓存
        return rows;
    }

    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     */
    public User findUser(String email){
        return userMapper.selectByEmail(email);
    }

    /**
     * 修改密码
     * @param email
     * @param password
     * @return  受影响的行数
     */
    public int resetPassword(String email, String password){
        //根据email查询用户信息，获取salt
        User user = userMapper.selectByEmail(email);
        //把新密码加上查询到的salt字段 生成新的加密密码
        password = CommunityUtil.md5(password + user.getSalt());
        //将生成的加密密码更新到数据库
        int rows = userMapper.updatePassword(user.getId(), password);
        clearCache(user.getId());   //数据发生更新，清除缓存
        return rows;
    }

    /**
     * 根据用户名返回user对象
     * @param username
     * @return
     */
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    /**
     * 1.优先从缓存中取值
     * @param userId
     * @return
     */
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 2.取不到时初始化缓存数据
     * @param userId
     * @return
     */
    private User initCache(int userId){
        //从数据库中查询用户信息
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        //将查询到用户信息保存到redis中，并设置过期时间为一小时 60秒 * 60分钟 = 3600
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3.数据变更时清除缓存数据
     * @param userId
     */
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }













}
