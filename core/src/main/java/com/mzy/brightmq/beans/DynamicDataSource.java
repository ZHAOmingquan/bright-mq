package com.mzy.brightmq.beans;

import com.mzy.brightmq.beans.enums.DBEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 *  com.mzy.brightmq.beans
 * @author BrightSpring 
 *10
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DBEnum> contextHolder = new ThreadLocal<>();

    /**
     * @Desc 初始化设置默认数据源
     * @author BrightSpring
     *19
     */
    public DynamicDataSource(Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(targetDataSources.get(DBEnum.DEFAULT_DB));
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
//        contextHolder.set(DBEnum.DEFAULT_DB);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        log.info("target datasource={}", getDataSource());
        return getDataSource();
    }

    public static void setDataSource(DBEnum dataSource) {
        if(dataSource==null){
            contextHolder.set(DBEnum.DEFAULT_DB);
        }else{
            contextHolder.set(dataSource);
        }
    }

    public static DBEnum getDataSource() {
        DBEnum db=contextHolder.get();
        if(db==null){
           db=DBEnum.DEFAULT_DB;
        }
        return db;
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }

}
