package com.hua.community.service;

import com.hua.community.dao.AlphaDao;
import com.hua.community.dao.DiscussPostMapper;
import com.hua.community.dao.UserMapper;
import com.hua.community.entity.DiscussPost;
import com.hua.community.entity.User;
import com.hua.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @create 2022-03-21 20:53
 */
@Service
//@Scope(value = "prototype")   //配置bean为多例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("构造方法初始化。。。");
    }

    @PostConstruct
    public void init(){
        System.out.println("init()初始化。。。");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁方法。。。");
    }

    public String find(){
        return alphaDao.select();
    }

    /**
     * 声明式：通过注解事务测试
     * propagation用于设置传播级别共有7个级别
     * REQUIRED: 支持当前事务（外部事务），如果不存在则创建新事务。比如a事务调用b（required）事务，如果a存在事务，则在b中以a的事务为主，否则b创建一个新事务
     * REQUIRED_NEW：创建一个新事务，并且暂停当前事务（外部事务）比如a事务调用b（required）事务，如果a存在事务，暂停a的事务，事务b创建一个新事务
     * NWSTED: 如果当前存在事务（外部事务）,则嵌套在该事务中执行（独立的提交和回滚），否则就会跟REQUIRED一样
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("a");   //引发异常

        return "ok";
    }

    /**
     * 编程式事务测试
     * @return
     */
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);  //设置隔离级别
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); //设置事务传播级别
        
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/941t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("测试二");
                post.setContent("新人报道！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("a");   //引发异常

                return "ok";
            }
        });
    }
}
