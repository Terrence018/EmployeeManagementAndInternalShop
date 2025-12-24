package com.empmall.exception;

import com.empmall.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局異常處理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result handleException(Exception e) {
        log.error("程序出錯啦~", e );
        return Result.error("出錯啦～請聯繫管理員");
    }

    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        log.error("程序出錯啦~", e );
        String message = e.getMessage();
        int i = message.indexOf("Duplicate entry");
        String errMsg =  message.substring(i);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2]+" 已存在");
        
    }
}
