package com.empmall.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class EmpQueryParam {
    private Integer page = 1;   //頁碼
    private Integer pageSize = 10; //每頁展示紀錄數
    private String name; //姓名
    private Integer gender; //性別
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin; //入職時間-開始
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end; //入職時間-結束


}

