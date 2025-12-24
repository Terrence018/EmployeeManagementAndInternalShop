package com.empmall.aop;

import com.empmall.mapper.OperateLogMapper;
import com.empmall.pojo.OperateLog;
import com.empmall.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Component
@Aspect
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    //環繞通知，紀錄日誌
    @Around("@annotation(com.empmall.anno.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        //執行目標方法
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        //計算花費時間
        long costTime = endTime - startTime;
        log.info("方法 {} 執行耗時：{} ms", joinPoint.getSignature(), costTime);

        //構建日誌實體
        OperateLog olog = new OperateLog();
        olog.setOperateEmpId(getCurrentUserId());
        olog.setOperateTime(LocalDateTime.now());
        olog.setClassName(joinPoint.getTarget().getClass().getName());
        olog.setMethodName(joinPoint.getSignature().getName());
        olog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        olog.setReturnValue(result != null ? result.toString() : "void");
        olog.setCostTime(costTime);

        //保存日誌
        log.info("紀錄操作日誌：{}", olog);
        operateLogMapper.insert(olog);

        return result;
    }

    /**
     * 獲取當前登入員工ID（
     */
    private Integer getCurrentUserId() {

        return CurrentHolder.getCurrentId();
    }

}
