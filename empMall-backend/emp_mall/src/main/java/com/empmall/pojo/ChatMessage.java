package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    // 訊息類型 (可以定義成 Enum，這裡先用 String 簡單處理)
    // 類型: CHAT(聊天), JOIN(加入), LEAVE(離開), TICKET_CREATE(建立工單)
    private String type;

    private String content;   // 訊息內容
    private String sender;    // 發送者 (使用者名稱或 ID)
    private String receiver;  // 接收者 (管理員名稱或 ID，若是廣播則為 null)
    private Long sessionId;   // 工單 ID / 聊天室 ID
    private LocalDateTime timestamp; // 傳送時間
}