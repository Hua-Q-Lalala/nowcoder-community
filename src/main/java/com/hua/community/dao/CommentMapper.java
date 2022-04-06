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
}

