package com.hua.community.controller;

import com.hua.community.annotation.LoginRequired;
import com.hua.community.entity.User;
import com.hua.community.service.UserService;
import com.hua.community.util.CommunityUtil;
import com.hua.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @create 2022-03-26 21:25
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;  //从配置文件里获取文件上传路径

    @Value("${community.path.domain}")
    private String domain;  //从配置文件里获取域名

    @Value("${server.servlet.context-path}")
    private String contextPath; //从配置文件里获取项目上下文路径

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 返回账号设置页面
     * @return
     */
    @LoginRequired  //标识此资源需要登录才能访问
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * RestFul风格
     * 用于将上传的图片保存到服务器
     *
     * 缺陷，没有限制图片上传的格式
     *
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired  //标识此资源需要登录才能访问
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        //验证后缀名是否为空
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的本地路径
        File dest = new File(uploadPath + "/" + fileName);

        //当目录不存在时，创建目录
        if(!dest.exists()){
            dest.mkdirs();
        }

        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        //更新当前用户的头像路径（web访问路径）
        //如：http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();   //从当前线程获取保存的用户对象
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        //更新到数据库
        userService.updateHeaderUrl(user.getId(), headerUrl);

        //更新头像后，将旧头像在服务器上删除
        String oldHeaderPath = uploadPath + user.getHeaderUrl().substring(user.getHeaderUrl().lastIndexOf("/"));
        File delFileName = new File(oldHeaderPath);
        System.out.println(oldHeaderPath);
        System.out.println(delFileName.delete());

        return "redirect:/index";
    }

    /**
     * 根据头像文件名从服务器固定图片保存路径下返回一张头像图片
     * @param fileName  //图片文件名
     * @param response  //利用response向浏览器返回图片
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;

        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix); //设置响应类型
        try(
                //JDK7 语法，在后面自动添加finally语句块，并在语句块中将输入输出类关闭
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os =  response.getOutputStream();
        ){
            //读取图片
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                //写出图片
                os.write(buffer, 0, b);
            }

        }catch (IOException e){
            logger.error("读取头像失败：" + e.getMessage());
        }

    }























}
