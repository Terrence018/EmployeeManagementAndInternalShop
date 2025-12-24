package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用於接收統計結果的視圖物件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpReportVO {
    private String categoryName; // 分類名稱 (例如: "男", "研發部")
    private Integer count;       // 統計數量
}