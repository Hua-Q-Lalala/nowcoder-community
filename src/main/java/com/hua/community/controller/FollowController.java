package com.hua.community.controller;

import com.hua.community.entity.Event;
import com.hua.community.entity.Page;
import com.hua.community.entity.User;
import com.hua.community.event.EventProducer;
import com.hua.community.service.FollowService;
import com.hua.community.service.UserService;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.CommunityUtil;
import com.hua.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @create 2022-04-08 0:33
 */
@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0, "已关注！");
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJsonString(0, "已取消关注！");
    }

    /**
     * 获取关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);

        //设置分页信息
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        //获取关注列表
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        //判断关注列表是否为空
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                //当前登录用户是否关注有这个用户
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 判断当前登录用户是否已关注userid这个用户
     * @param userId
     * @return
     */
    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null){
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);

        //设置分页信息
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));

        //获取粉丝列表
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        //判断粉丝列表是否为空
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                //当前登录用户是否关注有这个粉丝
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        model.addAttribute("users", userList);

        return "/site/follower";
    }
}
