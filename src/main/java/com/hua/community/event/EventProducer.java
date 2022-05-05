package com.hua.community.event;

import com.alibaba.fastjson.JSONObject;
import com.hua.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 事件生产者
 * @create 2022-04-30 14:38
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){
        //将事件发布到指定的主题, 将内容转为json格式方便消费者转化为java对象进行进一步处理
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }



















}
