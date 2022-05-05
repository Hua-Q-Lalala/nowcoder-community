package com.hua.community.dao;

import com.hua.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 查询私信
 * @create 2022-04-03 20:49
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话包含的私信数量
     * @param conversationId
     * @return
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量，分两种情况
     * 一是: 所有未读的数量
     * 二是：某一会话的未读数量
     * 根据conversationId动态拼接查询
     * @param userId
     * @param conversationId
     * @return
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 新增信息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改信息状态
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids, int status);

    /**
     * 查询user下某主题中最新的一条系统通知
     * @param userId
     * @param topic
     * @return
     */
    Message selectLastNotice(int userId, String topic);

    /**
     * 查询user下某主题的通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeCount(int userId, String topic);

    /**
     * 查询user下某主题中未读的通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeUnreadCount(int userId, String topic);

    /**
     * 查询某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
