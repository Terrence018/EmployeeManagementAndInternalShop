package com.empmall.service;

import com.empmall.pojo.PageResult;
import com.empmall.pojo.ProductLog;

public interface ProductLogService {

    //插入日誌
    void insertLog(ProductLog productLog);

    //分頁查詢日誌
    PageResult<ProductLog> page(Integer page, Integer pageSize);
}
