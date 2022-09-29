package com.mzy.brightmq.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  com.mzy.brightmq.config
 * @author BrightSpring 
 *4
 */
@Data
@ConfigurationProperties(prefix = "bright-mq")
public class MqDataSourceProperties extends HikariConfig {
    private String binlogServiceId;
}
