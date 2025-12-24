package com.empmall.controller;

import com.empmall.pojo.ChatMessage;
import com.empmall.pojo.ChatMessageLog;
import com.empmall.pojo.ChatSession;
import com.empmall.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
// @CrossOrigin // 如果前端在不同 Port (例如 5173)，則使用
public class ChatController {

    @Autowired
    private ChatService chatService;

    // -- HTTP REST API (工單流程) --

    // 1. 使用者發起工單
    // POST /api/chat/start
    @PostMapping("/start")
    public Long startChat(@RequestBody Map<String, Object> payload) {
        // 從前端接收 JSON: { "userId": 1, "userName": "小明", "category": "訂單", "topic": "訂單沒收到" }
        Long userId = Long.valueOf(payload.get("userId").toString());
        String userName = payload.get("userName").toString();
        String category = payload.get("category").toString();
        String topic = payload.get("topic").toString();

        return chatService.createSession(userId, userName, category, topic);
    }

    // 2. 管理員獲取等待中的工單列表
    // GET /api/chat/waiting
    @GetMapping("/waiting")
    public List<ChatSession> getWaitingList() {
        return chatService.getWaitingSessions();
    }

    // 3. 管理員接受工單
    // PUT /api/chat/accept/{sessionId}?adminId=5
    @PutMapping("/accept/{sessionId}")
    public void acceptChat(@PathVariable Long sessionId, @RequestParam Long adminId) {
        chatService.acceptSession(sessionId, adminId);
    }

    // 4. 獲取歷史訊息
    // GET /api/chat/history/{sessionId}
    @GetMapping("/history/{sessionId}")
    public List<ChatMessageLog> getHistory(@PathVariable Long sessionId) {
        return chatService.getSessionHistory(sessionId);
    }

    // 5. 結束對話
    // PUT /api/chat/close/{sessionId}
    @PutMapping("/close/{sessionId}")
    public void closeChat(@PathVariable Long sessionId) {
        chatService.closeSession(sessionId);
    }

    // --WebSocket API (即時聊天)--

    // 當前端發送訊息到 "/app/sendMessage" 時，會執行這裡
    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload ChatMessage message) {
        // 呼叫 Service 處理 (存檔 + 廣播)
        chatService.handleChatMessage(message);
    }
}