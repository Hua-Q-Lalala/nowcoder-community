package com.hua.community;

import com.hua.community.dao.DiscussPostMapper;
import com.hua.community.dao.UserMapper;
import com.hua.community.entity.DiscussPost;
import com.hua.community.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.UpperCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @create 2022-03-22 12:08
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        System.out.println(userMapper.selectById(11));

        System.out.println(userMapper.selectByName("nowcoder11"));

        System.out.println(userMapper.selectByEmail("nowcoder11@sina.com"));
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("asdf");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/15.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user);
    }

    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "qqwwee123");
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://images.nowcoder.com/999.png");
        System.out.println(rows);

    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(11, 0, 10);
        for(DiscussPost lis : list){
            System.out.println(lis);
        }

        int rows = discussPostMapper.selectDiscussPostRows(11);
        System.out.println(rows);
    }

























}



