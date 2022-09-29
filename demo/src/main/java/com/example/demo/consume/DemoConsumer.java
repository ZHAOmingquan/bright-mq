package com.example.demo.consume;

import com.mzy.brightmq.beans.Message;
import com.mzy.brightmq.service.BrightMQConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  消费者消费消息，必须实现接口BrightMQConsume。并在@Service注解中指定topic
 * @author BrightSpring 
 *15
 */
@Slf4j
@Service("demo_message")
public class DemoConsumer implements BrightMQConsumer {
    @Override
    public boolean consumeMessage(Message message) {
        log.info("检测到一条消息，我要消费了={}",message);
        return true;
    }
}
