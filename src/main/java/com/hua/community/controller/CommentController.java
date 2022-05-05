package com.hua.community.controller;

import com.hua.community.annotation.LoginRequired;
import com.hua.community.entity.Comment;
import com.hua.community.entity.DiscussPost;
import com.hua.community.entity.Event;
import com.hua.community.event.EventProducer;
import com.hua.community.service.CommentService;
import com.hua.community.service.DiscussPostService;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.print.PathGraphics;

import java.util.Date;

/**
 * @create 2022-04-01 13:38
 */
@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 添加评论
     * @param discussPostId
     * @param comment
     * @return
     */
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        
        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        //获取所评论实体的作者id
        if(comment.getEntityType() == ENTITY_TYPE_POST){    //实体为帖子，获取发布帖子的作者id
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){  //实体为评论，获取发布评论的作者id
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
