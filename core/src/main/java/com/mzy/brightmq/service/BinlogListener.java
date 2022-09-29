package com.mzy.brightmq.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.mzy.brightmq.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *  binlog监听器，监听并消费消息
 * 当监听到新增记录时，消费之。
 * @author BrightSpring 
 *6
 */
@Slf4j
@Component
public class BinlogListener implements CommandLineRunner {
    private final BinaryLogClient binaryLogClient;
    private final ConsumeStrategy consumeStrategy;
    public static Map<String,String> tableCache=new HashMap<>();

    public BinlogListener(BinaryLogClient binaryLogClient, ConsumeStrategy consumeStrategy) {
        this.binaryLogClient = binaryLogClient;
        this.consumeStrategy = consumeStrategy;
    }

    @Override
    public void run(String... args) {
        try {
            binaryLogClient.registerEventListener(event -> {
                EventData data = event.getData();
                if(data instanceof TableMapEventData){
                    TableMapEventData tableMapEventData = (TableMapEventData) data;
                    String tableId=String.valueOf(tableMapEventData.getTableId());
                    String tableName=tableMapEventData.getTable();
                    String oldName=tableCache.get(tableId);
                    if(oldName==null || !Objects.equals(oldName,tableName) ){
                        tableCache.put(tableId,tableName);
                    }
                }
                if (data instanceof WriteRowsEventData) {
                    WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
                    String tableName=tableCache.get(String.valueOf(writeRowsEventData.getTableId()));
                    log.info("Table={}", tableName);
                    if(tableName==null||tableName.length()==0){
                        log.info("not found table，tableId={}",writeRowsEventData.getTableId());
                        return ;
                    }
                    //仅当前服务进程下存在的消费者,才执行消费监听到的数据表
                    BrightMQConsumer consumer=consumeStrategy.getService(tableName);
                    if(consumer!=null){
                        List<Serializable[]> rows = writeRowsEventData.getRows();
                        for (Serializable[] row : rows) {
                            List<Serializable> entries = Arrays.asList(row);
                            Message message= new Message()
                                    .setId(Long.valueOf(entries.get(0).toString()))
                                    .setContent(entries.get(1).toString())
                                    .setBizId(entries.get(2).toString())
                                    .setStatus(Integer.valueOf(entries.get(3).toString()))
                                    .setFailedTimes(Integer.valueOf(entries.get(4).toString()))
                                    .setTableName(tableName);
                            log.info("listener the new message is ={}", message);
                            //消费之
                            consumeStrategy.consumeHandler(message);
                        }
                    }
                }
            });
            binaryLogClient.connect();
        } catch (IOException e) {
            log.info("binlog listener exception=", e);
        }
    }
}
