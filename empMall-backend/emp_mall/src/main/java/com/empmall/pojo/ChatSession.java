package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    private Long id;
    private Long userId;
    private String userName;
    private Long adminId;
    private String category; // 修改資料, 訂單問題...
    private String topic;    // 問題描述
    private Integer status;  // 0:等待中, 1:進行中, 2:已結束
    private LocalDateTime createTime;
    private LocalDateTime endTime;
}