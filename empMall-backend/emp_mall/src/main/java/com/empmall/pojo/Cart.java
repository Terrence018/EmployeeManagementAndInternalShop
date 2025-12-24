package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

//購物車
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer id;
    private Integer empId;
    private Integer productId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // --- 以下為聯表查詢需要的擴展欄位 (與資料庫對應，但非 cart 表欄位) ---
    private String productName;
    private String productImage;
    private Integer pointsNeeded;
    private Integer stock;
}