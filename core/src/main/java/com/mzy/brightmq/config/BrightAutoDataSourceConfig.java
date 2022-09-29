package com.mzy.brightmq.config;

import com.mzy.brightmq.beans.DynamicDataSource;
import com.mzy.brightmq.beans.enums.DBEnum;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *  com.mzy.brightmq.config
 * @author BrightSpring 
 *2
 */
@Slf4j
@Configuration
@Import(value = {MqDataSourceProperties.class})
public class BrightAutoDataSourceConfig {

    /**
     * @Desc 默认数据源
     * @author BrightSpring
     *13
     */
    @Bean("springDefaultDatasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * @Desc 消息队列数据源
     * @author BrightSpring
     *13
     */
    @Bean("brightMqDatasource")
    DataSource dataSource(MqDataSourceProperties mqDataSourceProperties) {
        return new HikariDataSource(mqDataSourceProperties);
    }

    /**
     * @Desc 动态数据源
     * @author BrightSpring
     *13
     */
    @Primary
    @Bean("targetDatasource")
    public DynamicDataSource targetDataSource(@Qualifier("springDefaultDatasource") DataSource masterDataSource, @Qualifier("brightMqDatasource") DataSource mqDataSource){
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(DBEnum.DEFAULT_DB,masterDataSource);
        targetDataSources.put(DBEnum.MQ_DB, mqDataSource);
        return new DynamicDataSource(targetDataSources);
    }

    /***
     * 当自定义数据源，用户必须注入，否则事务控制不生效
     */
    @Bean
    public DataSourceTransactionManager dataSourceTx(@Qualifier("targetDatasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
