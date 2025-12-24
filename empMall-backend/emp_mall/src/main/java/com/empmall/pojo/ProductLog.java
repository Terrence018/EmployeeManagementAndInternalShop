package com.empmall.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLog {
    private Integer id;
    private Integer operateUser; // 操作人ID
    private LocalDateTime operateTime;
    private String content; // 日誌內容

    // 非資料庫欄位，用於前端顯示操作人姓名
    private String operateName;
}