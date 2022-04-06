package com.hua.community.entity;

import java.util.Date;

/**
 * 私信实体类
 * @create 2022-04-03 20:44
 */
public class Message {

    private int id;
    private int fromId;     //发送方id     当fromId为1时，表示为系统通知
    private int toId;       //接收方id
    private String conversationId;  //会话id 由发送方和接收方id组成，格式为id_id 如111_123 规定id小的在下划线左边，大的在下划线右边
    private String content;     //私信内容
    private int status;     //0-未读;1-已读;2-删除;
    private Date createTime;       //创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}

