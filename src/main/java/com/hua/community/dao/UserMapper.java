package com.hua.community.dao;

import com.hua.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @create 2022-03-22 11:38
 */

@Mapper
public interface UserMapper {

    //根据id查找用户
    User selectById(int id);

    //根据用户名查找用户
    User selectByName(String username);

    //根据邮箱查找用户
    User selectByEmail(String email);

    //向数据库插入一个用户
    int insertUser(User user);

    //通过id修改用户的status状态
    int updateStatus(int id, int status);

    //根据id修改用户的头像地址
    int updateHeader(int id, String headerUrl);

    //根据id修改用户的密码
    int updatePassword(int id, String password);


}
