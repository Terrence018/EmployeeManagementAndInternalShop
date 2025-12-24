package com.empmall.service.impl;

import com.empmall.mapper.ProductLogMapper;
import com.empmall.pojo.PageResult;
import com.empmall.pojo.ProductLog;
import com.empmall.service.ProductLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductLogServiceImpl implements ProductLogService {

    @Autowired
    private ProductLogMapper productLogMapper;

    // 插入日誌
    public void insertLog(ProductLog productLog) {
        productLogMapper.insert(productLog);
    }

    // 分頁查詢日誌
    public PageResult<ProductLog> page(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<ProductLog> list = productLogMapper.list();
        Page<ProductLog> p = (Page<ProductLog>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }
}
