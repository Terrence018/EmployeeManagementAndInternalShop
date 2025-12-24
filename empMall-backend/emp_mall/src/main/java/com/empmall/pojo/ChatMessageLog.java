package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageLog {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createTime;
}