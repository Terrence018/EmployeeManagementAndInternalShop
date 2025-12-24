package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private Integer id;
    private Integer empId;        // 下單員工ID
    private Integer totalPoints;  //總消費
    private Integer productId;    // 商品ID
    private String productName;   // 商品名稱快照
    private Integer pointsSpent;  // 花費點數快照
    private Integer status;       // 1待處理, 2已出貨, 3已完成
    private Integer deliveryMethod; // 1自取, 2寄送
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String image;
    private String empName; // 下單員工姓名

    private List<OrderItem> items; //這筆訂單底下的所有商品明細
}