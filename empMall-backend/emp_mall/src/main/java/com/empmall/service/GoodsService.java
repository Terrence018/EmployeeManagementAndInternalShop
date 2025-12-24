package com.empmall.service;

import com.empmall.pojo.GoodsSalesVO;
import java.util.List;

public interface GoodsService {
    /**
     * 獲取商品銷量統計
     * @param orderBy 1=近期, 2=銷量
     */
    List<GoodsSalesVO> getSalesStats(Integer orderBy);
}