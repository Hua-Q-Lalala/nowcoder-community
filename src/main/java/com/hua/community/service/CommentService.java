package com.hua.community.service;

import com.hua.community.dao.CommentMapper;
import com.hua.community.entity.Comment;
import com.hua.community.util.CommunityConstant;
import com.hua.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @create 2022-03-31 22:18
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 获取评论列表
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 获取实体id为entityId， 类型为eneityType下的评论数量
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加评论，需要向评论表添加评论 同时更新帖子表中帖子的评论数量
     * 涉及两条SQL语句，所以需要进行事务控制，有一条执行失败则回滚
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //过滤html标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    /**
     * 查询某用户发布过的评论
     * @param userId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentsByUserId(int userId, int entityType, int offset, int limit){
        List<Comment> comments = commentMapper.selectCommentsByUserId(userId, ENTITY_TYPE_POST, offset, limit);
        return comments;
    }

    /**
     * 查询某用户发布的评论数量
     * @param userId
     * @param entityType
     * @return
     */
    public int findCommentCountByUserId(int userId, int entityType){
        return commentMapper.selectCommentCountByUserId(userId, entityType);
    }

    /**
     * 根据id返回一条comment记录
     * @param id
     * @return
     */
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}
