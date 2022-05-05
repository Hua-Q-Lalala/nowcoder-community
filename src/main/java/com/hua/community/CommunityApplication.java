package com.hua.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init(){
        //Redis和Elasticsearch都依赖Netty
        //如果Netty启动了，再启动会异常
        //这里解决netty启动冲突问题
        //看 Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
