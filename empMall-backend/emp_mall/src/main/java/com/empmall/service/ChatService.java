package com.empmall.service;

import com.empmall.pojo.ChatMessage;
import com.empmall.pojo.ChatMessageLog;
import com.empmall.pojo.ChatSession;
import java.util.List;

public interface ChatService {

    // 1. 建立工單
    Long createSession(Long userId, String userName, String category, String topic);

    // 2. 接受工單
    void acceptSession(Long sessionId, Long adminId);

    // 3. 處理並轉發訊息
    void handleChatMessage(ChatMessage message);

    // 4. 結束對話
    void closeSession(Long sessionId);

    // 5. 查詢等待中的工單
    List<ChatSession> getWaitingSessions();

    // 6. 查詢歷史訊息
    List<ChatMessageLog> getSessionHistory(Long sessionId);
}