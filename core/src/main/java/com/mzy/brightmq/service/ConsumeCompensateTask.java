package com.mzy.brightmq.service;

import com.mzy.brightmq.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *  消费补偿任务。
 * 当消费失败后的处理任务。
 * @author BrightSpring 
 *15
 */
@Slf4j
@Component
public class ConsumeCompensateTask {

    private final ConsumeStrategy consumeStrategy;
    private final MessageService messageService;
    private final CurrentLockService currentLockService;
    /**
     * @Desc 默认最大错误次数10次，错误10次后不再重试。
     */
    @Value("${bright-mq.consume-task.max-failed-times:10}")
    private Integer maxFailedTimes;

    public ConsumeCompensateTask(ConsumeStrategy consumeStrategy, MessageService messageService, CurrentLockService currentLockService) {
        this.consumeStrategy = consumeStrategy;
        this.messageService = messageService;
        this.currentLockService = currentLockService;
    }

    /**
     * @Desc fixedDelay执行时间间隔（毫秒）由开发者自定义到配置文件，来确定重复触发失败重试的频率。
     * 默认10秒重试一次，10次后不再重试。可自定义频率与最大失败次数。
     * 用户可自定义失败后的消费任务，需要在任务方法上加  @DS(type=DBEnum.MQ_DB) 切换数据源，处理消息数据库的逻辑。
     * 多线程处理：一个表一个线程处理
     * 本任务不会全库扫描，仅执行当前业务服务的消费者任务
     * @author BrightSpring
     *16
     */
    @Scheduled(fixedDelayString = "${bright-mq.consume-task.fixed-delay:10000}")
    public void consumeMessageTask() {
        //获取当前服务存在的消费策略，获取表，执行消费
        Map<String, BrightMQConsumer> strategyMap = consumeStrategy.getMap();
        if (!CollectionUtils.isEmpty(strategyMap)) {
            for (Map.Entry<String, BrightMQConsumer> map : strategyMap.entrySet()) {
                final String tableName=map.getKey();
                log.info("table is consume={}",tableName);
                //获取处理表的锁，保证当前表只有一个实例在处理，避免多实例并发处理
                boolean lock=currentLockService.tryLock(tableName);
                if(lock){
                    try{
                        CompletableFuture.runAsync(() -> {
                            List<Message> messageList = messageService.getFailedMessages(tableName, maxFailedTimes);
                            if (!CollectionUtils.isEmpty(messageList)) {
                                messageList.forEach(message -> {
                                    message.setTableName(tableName);
                                    //消费之
                                    consumeStrategy.consumeHandler(message);
                                });
                            }
                        });
                    }finally {
                        currentLockService.unLock(tableName);
                    }
                }else{
                    return;
                }
            }
        }
    }
}
