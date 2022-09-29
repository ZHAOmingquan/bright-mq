package com.mzy.brightmq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @Desc 线程池配置
 * @author BrightSpring
 * @Date 2022/3/7
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * @Desc 核心线程数
     * @author BrightSpring
     * @Date 2022/3/7
     */
    private int corePoolSize = 4;
    /**
     * @Desc 最大线程数
     * @author BrightSpring
     * @Date 2022/3/7
     */
    private int maximumPoolSize = 16;
    /**
     * @Desc 空闲等待时长
     * @author BrightSpring
     * @Date 2022/3/7
     */
    private int keepAliveTime = 60000;
    /**
     * @Desc 等待队列
     * @author BrightSpring
     * @Date 2022/3/7
     */
    private int queueCapacity = 1000;

    /**
     * @Desc 默认线程池  避免oom
     * @author BrightSpring
     * @Date 2022/3/7
     */
    @Bean
    public Executor taskExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     *@Desc 线程池
     * @author BrightSpring
     * @Date 2022/3/7
     */
    @Bean("brightMqTaskExecutor")
    public ThreadPoolExecutor fileCheckTaskExecutor() {
        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<> (queueCapacity);
        ThreadPoolTaskExecutor factory = new ThreadPoolTaskExecutor();
        //任务队列满了直接拒绝任务 等待下次执行
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, arrayBlockingQueue, factory);
        return poolExecutor;
    }
}
