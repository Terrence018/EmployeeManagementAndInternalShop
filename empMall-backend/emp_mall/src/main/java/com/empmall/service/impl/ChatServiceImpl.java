package com.empmall.service.impl;

import com.empmall.mapper.ChatMapper;
import com.empmall.pojo.ChatMessage;
import com.empmall.pojo.ChatMessageLog;
import com.empmall.pojo.ChatSession;
import com.empmall.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public Long createSession(Long userId, String userName, String category, String topic) {
        // 存入資料庫
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setUserName(userName);
        session.setCategory(category);
        session.setTopic(topic);
        chatMapper.insertSession(session);

        // 通知管理員
        String notification = "新工單: " + userName + " 詢問 " + category;
        messagingTemplate.convertAndSend("/topic/admin/notifications", notification);

        return session.getId();
    }

    @Override
    @Transactional
    public void acceptSession(Long sessionId, Long adminId) {
        ChatSession session = new ChatSession();
        session.setId(sessionId);
        session.setAdminId(adminId);
        session.setStatus(1);
        chatMapper.updateSession(session);

        // 通知使用者
        ChatMessage systemMsg = new ChatMessage();
        systemMsg.setType("JOIN");
        systemMsg.setContent("管理者已進入聊天室。");
        systemMsg.setSessionId(sessionId);
        systemMsg.setSender("SYSTEM");

        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, systemMsg);
    }

    @Override
    @Transactional
    public void handleChatMessage(ChatMessage message) {
        // 存入資料庫
        ChatMessageLog log = new ChatMessageLog();
        log.setSessionId(message.getSessionId());
        try {
            log.setSenderId(Long.parseLong(message.getSender()));
        } catch (Exception e) {
            log.setSenderId(0L);
        }
        log.setSenderName(message.getSender());
        log.setContent(message.getContent());
        chatMapper.insertMessage(log);

        //  轉發
        message.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat/" + message.getSessionId(), message);
    }

    @Override
    @Transactional
    public void closeSession(Long sessionId) {
        ChatSession session = new ChatSession();
        session.setId(sessionId);
        session.setStatus(2);
        session.setEndTime(LocalDateTime.now());
        chatMapper.updateSession(session);

        ChatMessage systemMsg = new ChatMessage();
        systemMsg.setType("LEAVE");
        systemMsg.setContent("對話已結束。");
        systemMsg.setSessionId(sessionId);
        systemMsg.setSender("SYSTEM");

        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, systemMsg);
    }

    @Override
    public List<ChatSession> getWaitingSessions() {
        return chatMapper.getWaitingSessions();
    }

    @Override
    public List<ChatMessageLog> getSessionHistory(Long sessionId) {
        return chatMapper.getMessagesBySessionId(sessionId);
    }
}