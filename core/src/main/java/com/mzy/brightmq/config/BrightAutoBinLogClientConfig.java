package com.mzy.brightmq.config;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *  com.mzy.brightmq.config
 * @author BrightSpring 
 *4
 */
@Slf4j
@Configuration
@Import(MqDataSourceProperties.class)
public class BrightAutoBinLogClientConfig {

    @Bean
    BinaryLogClient binaryLogClient(MqDataSourceProperties mqDataSourceProperties){
        String url= mqDataSourceProperties.getJdbcUrl();
        int lastColon=url.lastIndexOf(":");
        int lastSlash=url.lastIndexOf("/");
        String host=url.substring(13,lastColon);
        String portStr=url.substring(lastColon+1, lastSlash);
        int port = Integer.parseInt(portStr);
        log.info("host={},port={}",host,port);
        BinaryLogClient client = new BinaryLogClient(host, port,
                mqDataSourceProperties.getUsername(), mqDataSourceProperties.getPassword());
        client.setServerId(Long.parseLong(mqDataSourceProperties.getBinlogServiceId()));
        return client;
    }
}
