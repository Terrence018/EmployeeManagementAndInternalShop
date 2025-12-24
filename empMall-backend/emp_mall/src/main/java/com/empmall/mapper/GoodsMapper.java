package com.empmall.mapper;

import com.empmall.pojo.GoodsSalesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface GoodsMapper {

    /**
     * 統計商品銷量
     * @param orderBy 排序方式：1=按最近銷售時間(預設), 2=按總銷量
     */
    List<GoodsSalesVO> listGoodsSalesStats(Integer orderBy);
}