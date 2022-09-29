package com.mzy.brightmq.service;

import com.mzy.brightmq.beans.Message;

/**
 *  消费接口。消费者必须实现此接口来消费消息，并在实现类的@Service注解中命名为表名（即topic名称）。
 * 例如： @Service("test_message") 说明消息的topic为test_message并且消息将写入test_message表存储。
 * @author BrightSpring 
 *14
 */
public interface BrightMQConsumer {

    /**
     * @Desc 消费接口
     * @author BrightSpring
     *14
     */
    boolean consumeMessage(Message message);

}
