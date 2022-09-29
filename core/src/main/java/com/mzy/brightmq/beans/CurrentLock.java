package com.mzy.brightmq.beans;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *  com.mzy.brightmq.service
 * @author BrightSpring 
 *18
 */
@Data
public class CurrentLock {
    private String lockKey;
    private LocalDateTime createdTime;
}
