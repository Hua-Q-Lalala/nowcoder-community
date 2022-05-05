package com.hua.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.hua.community.entity.Message;
import com.hua.community.entity.Page;
import com.hua.community.entity.User;
import com.hua.community.service.DiscussPostService;
import com.hua.community.service.MessageService;
import com.hua.community.service.UserService;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.CommunityUtil;
import com.hua.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @create 2022-04-03 22:22
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     *查询会话列表
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> conversations = new ArrayList<>();

        if(conversationList != null){
            for (Message message : conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId()? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        //查询未读信息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    /**
     *查询会话内容列表
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){

        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters", letters);

        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取与当前用户进行私信的用户
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        } else{
            return userService.findUserById(id0);
        }

    }

    /**
     * 获取私信列表未读信息id
     * @param letterList
     * @return
     */
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for(Message message : letterList){
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }


    /**
     * 发送私信
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1, "发送失败，目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());

        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return  CommunityUtil.getJsonString(0);
    }

    /**
     * 删除私信消息
     * @param id
     * @return
     */
    @RequestMapping(path = "/letter/delete", method = RequestMethod.POST)
    @ResponseBody
    public String removeMessage(int id){

        int rows = messageService.deleteMessage(id);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 系统通知列表
     * @param model
     * @return
     */
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //评论通知
        Message message = messageService.findLastNotice(user.getId(), TOPIC_COMMENT);

        if(message != null) {
            //VO => View Object(视图对象)
            Map<String, Object> commentMapVO = new HashMap<>();

            commentMapVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            commentMapVO.put("user", userService.findUserById((Integer) data.get("userId")));
            commentMapVO.put("entityType", data.get("entityType"));
            commentMapVO.put("entityId", data.get("entityId"));
            commentMapVO.put("postId", data.get("postId"));

            //通知信息条数
            commentMapVO.put("count", messageService.findNoticeCount(user.getId(), TOPIC_COMMENT));
            //未读通知信息条数
            commentMapVO.put("unreadCount", messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT));

            model.addAttribute("commentNotice", commentMapVO);
        }


        //点赞通知
        message = messageService.findLastNotice(user.getId(), TOPIC_LIKE);

        if(message != null) {
            //VO => View Object(视图对象)
            Map<String, Object> likeMapVO = new HashMap<>();

            likeMapVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            likeMapVO.put("user", userService.findUserById((Integer) data.get("userId")));
            likeMapVO.put("entityType", data.get("entityType"));
            likeMapVO.put("entityId", data.get("entityId"));
            likeMapVO.put("postId", data.get("postId"));

            //通知信息条数
            likeMapVO.put("count", messageService.findNoticeCount(user.getId(), TOPIC_LIKE));
            //未读通知信息条数
            likeMapVO.put("unreadCount", messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE));

            model.addAttribute("likeNotice", likeMapVO);
        }


        //关注通知
        message = messageService.findLastNotice(user.getId(), TOPIC_FOLLOW);

        if(message != null) {
            //VO => View Object(视图对象)
            Map<String, Object> followMapVO = new HashMap<>();
            followMapVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            followMapVO.put("user", userService.findUserById((Integer) data.get("userId")));
            followMapVO.put("entityType", data.get("entityType"));
            followMapVO.put("entityId", data.get("entityId"));

            //通知信息条数
            followMapVO.put("count", messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW));
            //未读通知信息条数
            followMapVO.put("unreadCount", messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW));

            model.addAttribute("followNotice", followMapVO);
        }

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "site/notice";
    }

    /**
     * 系统通知详情列表
     * @param topic
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        //配置分页相关
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();

        if(noticeList != null) {
            //设置已读
            List<Integer> ids = getLetterIds(noticeList);
            if(!ids.isEmpty()) {
                messageService.readMessage(ids);
            }

            for (Message notice : noticeList){
                Map<String, Object> map = new HashMap<>();

                //通知
                map.put("notice", notice);
                //发送通知的系统用户
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("enetityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                noticeVoList.add(map);
            }
        }

        model.addAttribute("notices", noticeVoList);

        return "site/notice-detail";
    }

}



































