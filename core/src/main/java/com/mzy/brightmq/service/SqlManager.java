package com.mzy.brightmq.service;

import com.mzy.brightmq.beans.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  sql组装类
 * @author BrightSpring 
 *13
 */
@Slf4j
@Service
public class SqlManager {

    /**
     * @Desc 消息保存
     * @author BrightSpring
     *18
     */
    public static String getSaveSQL(Message message) {
        String sql = "INSERT INTO " +
                message.getTableName() +
                " (content,status,failed_times,biz_id,created_time,updated_time) VALUES ( " +
                "'" + message.getContent() + "'," +
                "'" + message.getStatus() + "'," +
                "'" + message.getFailedTimes() + "'," +
                "'" + message.getBizId() + "',NOW(),NOW())" ;
                //    "  on duplicate key update content= VALUES(content), status=VALUES(status) ,failed_times=VALUES(failed_times),updated_time=VALUES(updated_time) ";
        log.info("execute sql={}", sql);
        return sql;
    }

    /**
     * @Desc 批量消息保存
     * @author BrightSpring
     *18
     */
    public static String getSaveBatchSQL(List<Message> messageList) {
        StringBuilder sql=new StringBuilder();
        sql.append( "INSERT INTO " ).append(messageList.get(0).getTableName())
                .append(" (content,status,failed_times,biz_id,created_time,updated_time) VALUES ") ;
        int size=messageList.size();
        for(int i=0;i<size;i++){
            Message message=messageList.get(i);
            sql.append("('" ).append(message.getContent()).append( "'," )
                    .append("'").append(message.getStatus()).append("'," )
                    .append("'").append(message.getFailedTimes()).append("'," )
                    .append("'").append(message.getBizId()).append("', NOW(),NOW())" );
            if(i<size-1){
                sql.append(",");
            }
        }
        // sql.append(" on duplicate key update content= VALUES(content), status=VALUES(status) ,failed_times=VALUES(failed_times),updated_time=VALUES(updated_time)" );
        log.info("execute sql={}", sql);
        return sql.toString();
    }

    /**
     * @Desc 创建topic
     * @author BrightSpring
     *18
     */
    public static String createTopicTableSQL(String topicTable){
        String sql="CREATE TABLE IF NOT EXISTS " + topicTable + " LIKE default_message";
        log.info("execute sql={}", sql);
        return sql;
    }

    /**
     * @Desc 修改消息状态与失败次数
     * 不允许修改已经成功消费的消息状态。避免重复消费
     * @author BrightSpring
     *18
     */
    public static String updateMessageStatueSQL(Message message){
        String sql="UPDATE "+message.getTableName()+" SET status="+message.getStatus()+", failed_times="+message.getFailedTimes()+" WHERE id="+message.getId()+" and (status=0 or status=2)";
        log.info("execute sql={}", sql);
        return sql;
    }


    /**
     * @Desc 获取未消费的消息：包括消费失败的消息以及超过10分钟一次都没消费过的消息。
     * 查询失败次数未超过maxFailedTimes且距离当前时间最近的2000条消息
     * 若最近失败消息大于2000，则待最近的2000条失败次数超过maxFailedTimes时再被查询
     * 若最近失败消息不断累积，远超过2000且不断增加，无法被消费，则视为系统业务异常，
     * 需要开发者自行到数据库修复bug后由程序重新消费直到正常。若不需要这些消息则可设置这些消息为死信消息（failedTimes>maxFailedTimes）或删除之。
     * 业务bug不能由消息中心来承担纠错。
     * 超过10分钟一次都未消费的数据可能是程序异常挂掉，导致监听程序未运行而积累的未消费的数据，也需要查询出执行消费。
     * @author BrightSpring
     *18
     */
    public static String getFailedMessagesSQL(String tableName, Integer maxFailedTimes){
        String sql="SELECT t.id,t.content,t.biz_id as bizId,t.status,t.failed_times as failedTimes FROM "+
                tableName+" t WHERE (t.status=2 and t.failed_times<"+
                maxFailedTimes+") OR (t.status=0 AND t.created_time<DATE_SUB(now(),INTERVAL 10 MINUTE)) ORDER BY t.created_time DESC LIMIT 2000";
        log.info("execute sql={}", sql);
        return sql;
    }

    /**
     * @Desc 加锁sql，即是插入
     * @author BrightSpring
     *18
     */
    public static String getLockSQL(String key){
        String sql="INSERT INTO current_lock(lock_key,created_time) VALUE ('"+key+"',now())";
        log.info("execute sql={}", sql);
        return sql;
    }

    /**
     * @Desc 解锁sql，即是删除
     * @author BrightSpring
     *18
     */
    public static String getUnLockSQL(String key){
        String sql="DELETE FROM current_lock WHERE lock_key='"+key+"'";
        log.info("execute sql={}", sql);
        return sql;
    }
}
