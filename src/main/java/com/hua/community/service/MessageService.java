package com.hua.community.service;

import com.hua.community.dao.MessageMapper;
import com.hua.community.entity.Message;
import com.hua.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @create 2022-04-03 22:03
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询用户所有的会话
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * 查询用户会话数量
     * @param userId
     * @return
     */
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询某个会话内容列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * 查询会话内容数量
     * @param conversationId
     * @return
     */
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询未读数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 添加私信
     * @param message
     * @return
     */
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 将消息设置为已读
     * @param ids
     * @return
     */
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids, 1);
    }

    /**
     * 将消息状态设置为删除
     * @param id
     * @return
     */
    public int deleteMessage(int id) {
        return messageMapper.updateStatus(Arrays.asList(new Integer[]{id}), 2);
    }

    /**
     * 查找最新一条系统通知
     * @param userId
     * @param topic
     * @return
     */
    public Message findLastNotice(int userId, String topic){
        return messageMapper.selectLastNotice(userId, topic);
    }

    /**
     * 查找系统通知数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(int userId, String topic){
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * 查找未读的系统通知数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeUnreadCount(int userId, String topic){
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    /**
     * 查询某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findNotices(int userId, String topic, int offset, int limit){
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
