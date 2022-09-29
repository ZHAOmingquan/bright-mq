package com.example.demo.product;

import com.mzy.brightmq.beans.Message;
import com.mzy.brightmq.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  com.example.demo.product
 * @author BrightSpring 
 *18
 */
@Slf4j
@Service
public class DemoProducer {
    @Autowired
    private MessageService messageService;

    /**
     * @Desc 发消息
     * @author BrightSpring
     *18
     */
    public void createMessage(){
        //创建一个default_message存储的消息
        Message message= new Message()
                //必填字段，指定存储表（类似topic，一个表存储同一个类型的消息）
                .setTableName("default_message")
                //必填字段
                .setContent("任意的消息")
                //选填字段，自定义的业务唯一ID
                .setBizId(UUID.randomUUID().toString());
        //发送消息（保存消息）
        messageService.sendMessage(message);
    }

    /**
     * @Desc 批量发送消息
     * @author BrightSpring
     *18
     */
    public void createManyMessage(){
        List<Message> list=new ArrayList<>();
        for(int i=0;i<600;i++){
            Message message=new Message()
            .setTableName("default_message")
            .setContent("消息"+ i);
            list.add(message);
        }
        messageService.sendBatchMessage(list);
    }
    /**
     * @Desc 创建topic，此步骤可自行在数据库创建，create table if not exists demo_message like default_message;
     * @author BrightSpring
     *18
     */
    public void createTopic(String topic){
        //发送消息（保存消息）
        messageService.createTopic(topic);
    }

    /**
     * @Desc 同时发送多个topic消息。
     * @author BrightSpring
     *18
     */
    public void sendManyTopic(){
        Message message1= new Message()
                //必填字段，指定存储表（类似topic，一个表存储同一个类型的消息）
                .setTableName("default_message")
                //必填字段
                .setContent("{一种消息格式}")
                //选填字段，自定义的业务唯一ID
                .setBizId(UUID.randomUUID().toString());
        Message message2=new Message()
                //必填字段，指定存储表（类似topic，一个表存储同一个类型的消息）
                .setTableName("demo_message")
                //必填字段
                .setContent("[另一种消息格式]")
                //选填字段，自定义的业务唯一ID
                .setBizId(UUID.randomUUID().toString());
        messageService.sendMessage(message1);
        messageService.sendMessage(message2);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;
    /**
     * @Desc 一个消息无关的业务逻辑。检查是否正常使用业务数据库，而非消息数据库。
     * @author BrightSpring
     *19
     */
    public void business(){
        //业务库的一个message表
        Message message=jdbcTemplate.queryForObject("select * from test_message t where t.id =14",new BeanPropertyRowMapper<>(Message.class));
        log.info("message is ={}",message);
    }
}
