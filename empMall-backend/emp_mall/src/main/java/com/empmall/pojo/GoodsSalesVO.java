package com.empmall.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GoodsSalesVO {
    private Integer id;           // 商品ID
    private String name;          // 商品名稱
    private String image;         // 商品圖片
    private Integer totalSold;    // 總銷量
    private LocalDateTime lastSaleTime; // 最近一次銷售時間
}