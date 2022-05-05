package com.hua.community.dao;

import com.hua.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * 过时的类，用于向数据库中插入用户登录的ticket, 已利用redis重构
 * @create 2022-03-26 13:08
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    /**
     * 插入一条凭证
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired}) "
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 查询登录凭证ticket
     * @param ticket
     * @return
     */
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 采用注解方式编写sql语句，并包含动态sql，在编写动态sql时，需要使用<script></script>将sql语句括起来
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "<script>",
                "update login_ticket set status = #{status} where ticket = #{ticket}",
                "<if test=\"ticket!=null\">",
                    "and 1=1",
                "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
