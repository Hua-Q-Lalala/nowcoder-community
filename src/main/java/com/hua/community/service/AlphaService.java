package com.hua.community.service;

import com.hua.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @create 2022-03-21 20:53
 */
@Service
//@Scope(value = "prototype")   //配置bean为多例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

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
}
