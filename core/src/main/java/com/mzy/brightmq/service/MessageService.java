package com.mzy.brightmq.service;

import com.mzy.brightmq.annotation.DS;
import com.mzy.brightmq.beans.Message;
import com.mzy.brightmq.beans.enums.DBEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  com.mzy.brightmq.service
 * @author BrightSpring 
 *7
 */
@Slf4j
@Service
public class MessageService {
    private final JdbcTemplate jdbcTemplate;

    public MessageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @Desc 保存单个消息
     * @author BrightSpring
     *14
     */
    @DS(type=DBEnum.MQ_DB)
    public boolean sendMessage(Message message) {
        try {
            jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.getSaveSQL(message)));
        }catch (Exception e){
            log.error("save message error=",e);
            return false;
        }
        return true;
    }

    /**
     * @Desc 批量保存消息
     * @author BrightSpring
     *14
     */
    @DS(type=DBEnum.MQ_DB)
    public boolean sendBatchMessage(List<Message> messageList) {
        if(CollectionUtils.isEmpty(messageList)){
            return true;
        }
        try{
            //500条一次
            List<List<Message>> all=splitList(messageList,500);
            all.forEach(list->jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.getSaveBatchSQL(list))));
        }catch (Exception e){
            log.error("save message error=",e);
            return false;
        }
        return true;
    }

    @DS(type=DBEnum.MQ_DB)
    public boolean updateMessageStatus(Message message) {
        try{
            jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.updateMessageStatueSQL(message)));
        }catch (Exception e){
            log.error("update message status error=",e);
            return false;
        }
        return true;
    }

    /**
     * @Desc 创建一个topic
     * @author BrightSpring
     *15
     */
    @DS(type=DBEnum.MQ_DB)
    public boolean createTopic(String topicTable){
        try {
            jdbcTemplate.update(connection -> connection.prepareStatement(SqlManager.createTopicTableSQL(topicTable)));
        }catch (Exception e){
            log.error("create topic error=",e);
            return false;
        }
        return true;
    }


    @DS(type=DBEnum.MQ_DB)
    public List<Message> getFailedMessages(String tableName, Integer maxFailedTimes){
        try {
            return jdbcTemplate.query(SqlManager.getFailedMessagesSQL(tableName,maxFailedTimes),new BeanPropertyRowMapper<>(Message.class));
        }catch (Exception e){
            log.error("query failed message error=",e);
        }
        return new ArrayList<>();
    }


    /**
     * @Desc 拆分列表
     * @author BrightSpring
     *15
     */

    private <T>List<List<T>> splitList(List<T> list,int splitSize){
        //计算分割后的子list个数
        int maxSize = (list.size() + splitSize - 1) / splitSize;
        //开始分割
        return Stream.iterate(0, n -> n + 1)
                .limit(maxSize)
                .parallel()
                .map(a -> list.parallelStream().skip(a * splitSize).limit(splitSize).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());
    }
}
