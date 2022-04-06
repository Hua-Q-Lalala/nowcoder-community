package com.hua.community.service;

import com.hua.community.dao.DiscussPostMapper;
import com.hua.community.entity.DiscussPost;
import com.hua.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @create 2022-03-22 14:49
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;    //过滤敏感词

    /**
     * 查询所有帖子
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 根据userid查询用户发布了多少条帖子
     * @param userId
     * @return
     */
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 插入帖子
     * @param post
     * @return
     */
    public int addDiscussPost(DiscussPost post){
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义HTML标记
        //post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        //post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));


        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 根据id查询指定帖子
     * @param postId
     * @return
     */
    public DiscussPost findDiscussPostById(int postId){
        return discussPostMapper.selectDiscussPostById(postId);
    }

    /**
     * 更新帖子评论数量
     * @param id
     * @param commentCount
     * @return
     */
    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

}

