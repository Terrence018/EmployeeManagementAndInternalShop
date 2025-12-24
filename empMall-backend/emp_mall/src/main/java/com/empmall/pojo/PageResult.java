package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//分頁結果封裝類
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult <T> {
    private Long total; //總數量
    private List<T> rows;

}
