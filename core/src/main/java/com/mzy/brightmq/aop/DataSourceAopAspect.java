package com.mzy.brightmq.aop;

import com.mzy.brightmq.annotation.DS;
import com.mzy.brightmq.beans.DynamicDataSource;
import com.mzy.brightmq.beans.enums.DBEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 *  com.mzy.brightmq.aop
 * @author BrightSpring 
 *10
 */
@Slf4j
@Aspect
@Component
public class DataSourceAopAspect {

    @Pointcut("@annotation(com.mzy.brightmq.annotation.DS)")
    public void dataSourceAopAspect(){

    }

    /**
     * 切换数据源
     */
    @Around("dataSourceAopAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        DS ds = method.getAnnotation(DS.class);
        DBEnum dbType = ds.type();
        log.info("use datasource={}",dbType);
        DynamicDataSource.setDataSource(dbType);
        try {
            return joinPoint.proceed();
        } finally {
            //设置回默认数据库
            DynamicDataSource.clearDataSource();
        }
    }
}
