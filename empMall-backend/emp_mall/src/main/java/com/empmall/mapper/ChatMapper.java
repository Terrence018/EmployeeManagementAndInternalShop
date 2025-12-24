package com.empmall.mapper;

import com.empmall.pojo.ChatMessageLog;
import com.empmall.pojo.ChatSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMapper {

    // 1. 建立新工單 (回傳 id)
    @Insert("INSERT INTO chat_session(user_id, user_name, category, topic, status, create_time) " +
            "VALUES(#{userId}, #{userName}, #{category}, #{topic}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSession(ChatSession session);

    // 2. 更新工單狀態 (管理員接單 或 結束對話)
    @Update("UPDATE chat_session SET admin_id = #{adminId}, status = #{status}, end_time = #{endTime} WHERE id = #{id}")
    void updateSession(ChatSession session);

    // 3. 查詢所有等待中的工單 (給管理員看)
    @Select("SELECT * FROM chat_session WHERE status = 0 ORDER BY create_time ASC")
    List<ChatSession> getWaitingSessions();

    // 4. 儲存聊天訊息
    @Insert("INSERT INTO chat_message(session_id, sender_id, sender_name, content, create_time) " +
            "VALUES(#{sessionId}, #{senderId}, #{senderName}, #{content}, NOW())")
    void insertMessage(ChatMessageLog message);

    // 5. 查詢某個工單的歷史訊息
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessageLog> getMessagesBySessionId(Long sessionId);

    // 6. 根據 ID 查工單
    @Select("SELECT * FROM chat_session WHERE id = #{id}")
    ChatSession getSessionById(Long id);
}