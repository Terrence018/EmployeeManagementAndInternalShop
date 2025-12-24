package com.empmall.pojo;

import lombok.Data;

@Data
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String image;
    private Integer quantity;
    private Integer pointsPerItem; // 對應資料庫 points_per_item
}