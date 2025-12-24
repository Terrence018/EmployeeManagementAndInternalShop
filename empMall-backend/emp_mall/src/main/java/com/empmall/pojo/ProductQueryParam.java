package com.empmall.pojo;

import lombok.Data;

@Data
public class ProductQueryParam {
    private Integer page = 1;
    private Integer pageSize = 10;

    private String keyword;   // 關鍵字搜尋
    private Integer status;   // 狀態篩選 (1=上架, null=全部)
    private Integer category; // 分類篩選
}