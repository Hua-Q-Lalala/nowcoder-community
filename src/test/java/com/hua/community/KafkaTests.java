package com.hua.community;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * @create 2022-04-28 23:57
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test1", "111");
        kafkaProducer.sendMessage("test1", "222");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 生产者
 */
@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发布信息
     * @param topic
     * @param content
     */
    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }

}

/**
 * 消费者（被动接受信息，有则读取，无则阻塞）
 */
@Component
class KafkaConsumer{

    /**
     * 监听test1主题
     * @param record
     */
    @KafkaListener(topics = {"test1"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}