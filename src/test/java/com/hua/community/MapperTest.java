package com.hua.community;

import com.hua.community.dao.DiscussPostMapper;
import com.hua.community.dao.LoginTicketMapper;
import com.hua.community.dao.MessageMapper;
import com.hua.community.dao.UserMapper;
import com.hua.community.entity.DiscussPost;
import com.hua.community.entity.LoginTicket;
import com.hua.community.entity.Message;
import com.hua.community.entity.User;
import com.mysql.cj.log.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.UpperCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PutMapping;

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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 测试LoginTicketMapper
     */
    @Test
    public void testInsertLoginTicket(){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(101);
        ticket.setTicket("abc");
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(ticket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        int abc = loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

    }

    /**
     * 测试帖子插入的方法
     */
    @Test
    public void testInsertDiscussPost(){
        DiscussPost post = new DiscussPost();
        post.setUserId(149);
        post.setTitle("测试数据1");
        post.setContent("2022-03-31的测试数据");
        post.setType(0);
        post.setStatus(0);
        post.setCreateTime(new Date());
        post.setCommentCount(0);
        post.setScore(0);

        int rows = discussPostMapper.insertDiscussPost(post);
        System.out.println("插入成功");
    }

    /**
     * 测试私信mapper
     */

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectLetters(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);

        System.out.println("所有会话：");
        for(Message message : messages){
            System.out.println(message);
        }

        int i = messageMapper.selectConversationCount(111);
        System.out.println("私信总数" + i);

        //某会话所有信息
        System.out.println("某会话所有私信内容：");
        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for(Message message : messages1){
            System.out.println(message);
        }

        System.out.println("某个会话包含的私信数量");
        int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println("某个会话包含的私信数量" + i1);

        System.out.println("查询未读私信的数量");
        int i2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println("查询未读私信的数量" + i2);
    }

















}



