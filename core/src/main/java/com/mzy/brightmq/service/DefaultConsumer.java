package com.mzy.brightmq.service;

import com.mzy.brightmq.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  默认消费者。
 * 此默认消费者消费default_message表的数据
 * @author BrightSpring 
 *14
 */
@Slf4j
@Service("default_message")
public class DefaultConsumer implements BrightMQConsumer {
    @Override
    public boolean consumeMessage(Message message) {
        log.info("default consumer message={}",message);
        return true;
    }
}
