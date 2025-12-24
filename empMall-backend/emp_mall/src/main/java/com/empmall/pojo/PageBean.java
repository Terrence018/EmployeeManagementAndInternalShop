package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {
    private Long total;    // 總記錄數 (給分頁元件顯示用)
    private List<T> items; // 當前頁數據列表 (給表格顯示用)
}