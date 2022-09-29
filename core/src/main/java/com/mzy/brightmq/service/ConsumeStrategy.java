package com.mzy.brightmq.service;

import com.mzy.brightmq.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  消费策略
 * @author BrightSpring 
 *14
 */
@Slf4j
@Service("consumeStrategy")
public class ConsumeStrategy {
    @Autowired
    private MessageService messageService;
    @Autowired
    private final Map<String, BrightMQConsumer> strategyMap = new ConcurrentHashMap<>();

    public BrightMQConsumer getService(String topicTable) {
        return strategyMap.get(topicTable);
    }

    public Map<String, BrightMQConsumer> getMap() {
        return strategyMap;
    }

    /**
     * @Desc 消费消息处理
     * @author BrightSpring
     *19
     */
    public void consumeHandler(Message message){
        try {
            boolean isSuccess =getService(message.getTableName()).consumeMessage(message);
            if (isSuccess) {
                message.setStatus(Message.Status.CONSUMED);
            } else {
                message.setStatus(Message.Status.FAILED_CONSUMED);
                message.setFailedTimes(message.getFailedTimes() + 1);
            }
        } catch (Exception e) {
            message.setStatus(Message.Status.FAILED_CONSUMED);
            message.setFailedTimes(message.getFailedTimes() + 1);
            log.error("consumed failed ={}", message);
        }
        messageService.updateMessageStatus(message);
    }
}
