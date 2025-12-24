package com.empmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 設定連線端點 (Handshake Endpoint)
        // 前端連線時要對應這個網址：ws://localhost:8080/ws-endpoint
        registry.addEndpoint("/ws-endpoint")
                .setAllowedOriginPatterns("http://localhost:5173") // 允許跨域 (因為你的 Vue 可能跑在 5173 埠)
                .withSockJS(); // 啟用 SockJS 支援 (若瀏覽器不支援 WebSocket 自動降級)
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 設定訊息代理 (Message Broker)
        // "/topic" 用於廣播 (一對多，例如：全體廣播、管理員通知)
        // "/user"  用於點對點 (一對一，例如：私訊)
        registry.enableSimpleBroker("/topic", "/user");

        // 3. 設定前端發送訊息的前綴
        // 前端送訊息給後端時，網址要加這個前綴，例如：/app/chat
        registry.setApplicationDestinationPrefixes("/app");

        // 4. 設定點對點推播的前綴 (預設就是 /user，這裡顯式宣告一下)
        registry.setUserDestinationPrefix("/user");
    }
}