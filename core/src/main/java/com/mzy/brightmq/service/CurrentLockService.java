package com.mzy.brightmq.service;

import com.mzy.brightmq.annotation.DS;
import com.mzy.brightmq.beans.enums.DBEnum;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *  基于数据库的分布式锁实现
 * 加锁：key作为唯一键插入一条记录，相同的key就无法插入
 * 解锁：删除key这条记录
 * @author BrightSpring 
 *18
 */
@Service
public class CurrentLockService {
    private final JdbcTemplate jdbcTemplate;

    public CurrentLockService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @Desc lock
     * @author BrightSpring
     *18
     */
    @DS(type= DBEnum.MQ_DB)
    public boolean tryLock(String key){
        try{
            jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.getLockSQL(key)));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @Desc unLock
     * @author BrightSpring
     *18
     */
    @DS(type=DBEnum.MQ_DB)
    public boolean unLock(String key){
        try{
            jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.getUnLockSQL(key)));
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
