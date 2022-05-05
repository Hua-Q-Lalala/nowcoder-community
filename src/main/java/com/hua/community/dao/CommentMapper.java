package com.hua.community.dao;

import com.hua.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @create 2022-03-31 21:48
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据实体类型、实体id，查询从offset行开始limit条的评论
     * @param entityType    实体类型 有帖子实体，或者评论中的评论实体，或者其他
     * @param entityId  //实体的id
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 根据实体类型，实体id查询有多少条评论
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 添加评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据userId查询该用户发布过的评论，分页显示
     * @param userId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByUserId(int userId, int entityType, int offset, int limit);

    /**
     * 查询userId的用户发布过多少条评论
     * @param userId
     * @param entityType
     * @return
     */
    int selectCommentCountByUserId(int userId, int entityType);

    /**
     * 根据id查询一条comment
     * @param id
     * @return
     */
    Comment selectCommentById(int id);
}

