package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Products {
    private Integer id;
    private String name;
    private String image;
    private String description;
    private Integer pointsNeeded; // 對應資料庫 points_needed
    private Integer status;       // 1 上架, 0 下架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer category; //分類欄位
    private Integer stock; // 庫存數量
}

