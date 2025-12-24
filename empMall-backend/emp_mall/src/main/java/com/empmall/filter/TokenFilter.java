package com.empmall.filter;

import com.empmall.utils.CurrentHolder;
import com.empmall.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURI();

        // 放行登入接口 和 WebSocket 連線接口
        if (url.contains("/login") || url.startsWith("/ws-endpoint")) {
            filterChain.doFilter(request, response);
            return;
        }

        //1. 強制設定 CORS 跨域 Headers (解決瀏覽器報錯的核心)
        response.setHeader("Access-Control-Allow-Origin", "*"); // 允許所有來源
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, token, Authorization");

        //2. 放行 OPTIONS 預檢請求
        // 遇到 OPTIONS 請求，直接回傳 200 OK 並結束，不要往下傳遞，避免被後面的邏輯誤判為 401
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 3. 獲取到請求路徑
        String requestURI = request.getRequestURI();

        // 4. 判斷是否是登錄請求，包含 /login 或其他公開路徑直接放行
        if(requestURI.contains("/login") ||
                requestURI.contains("/email/send") ||
                requestURI.contains("/emps/reset-password") ||
                requestURI.contains("/upload")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 5. 獲取請求頭中 token
        String token = request.getHeader("token");

        // 6. 判斷 token 是否存在
        if(!StringUtils.hasLength(token)){
            log.info("令牌為空, 響應401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 7. 校驗令牌
        try {
            Claims claims = JwtUtils.parseToken(token); // 確保你的工具類方法名是對的 (parseToken vs parseJWT)

            // 獲取 ID
            Integer empId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(empId);

            // 獲取 Role 並存入 ThreadLocal
            Object roleObj = claims.get("role");
            if (roleObj != null) {
                Integer role = Integer.valueOf(roleObj.toString());
                CurrentHolder.setCurrentRole(role);
                log.info("當前用戶ID: {}, 角色Role: {} (1=管理員, 2=員工)", empId, role);
            }
        } catch (Exception e) {
            log.info("令牌非法或解析失敗, 響應401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 8. 校驗通過， 放行
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 9. 請求結束後，務必刪除 ThreadLocal 中的數據，防止內存洩漏
            CurrentHolder.remove();
        }
    }
}