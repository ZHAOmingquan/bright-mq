package com.mzy.brightmq.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  com.mzy.brightmq.beans
 * @author BrightSpring 
 *2
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    @Id
    private Long id;
    private String content;
    private String bizId;
    private Integer status=Status.NO_CONSUMED;
    private Integer failedTimes=0;
    private LocalDateTime createdTime=LocalDateTime.now();
    private LocalDateTime updatedTime=LocalDateTime.now();
    /**
     * 非表字段：== topic名
     */
    @Transient
    private String tableName;

    public static class Status{
        /**
         * 未消费
         */
        public final static Integer NO_CONSUMED=0;
        /**
         * 消费成功
         */
        public final static Integer CONSUMED=1;
        /**
         * 消费失败
         */
        public final static Integer FAILED_CONSUMED=2;
    }
}
