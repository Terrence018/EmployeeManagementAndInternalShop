package com.empmall.pojo;

import lombok.Data;

@Data
public class OrderRequest {
    private Integer empId;          // 誰要換 (從Token抓)
    private Integer productId;      // 換什麼
    private Integer deliveryMethod; // 1自取, 2寄送
    private String address;         // 地址
}