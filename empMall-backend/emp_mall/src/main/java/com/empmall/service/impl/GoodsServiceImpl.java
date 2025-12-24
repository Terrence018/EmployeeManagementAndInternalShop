package com.empmall.service.impl;

import com.empmall.mapper.GoodsMapper;
import com.empmall.pojo.GoodsSalesVO;
import com.empmall.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsSalesVO> getSalesStats(Integer orderBy) {
        // 如果前端沒傳排序參數，預設使用 1 (按時間排序)
        if (orderBy == null) {
            orderBy = 1;
        }
        return goodsMapper.listGoodsSalesStats(orderBy);
    }
}