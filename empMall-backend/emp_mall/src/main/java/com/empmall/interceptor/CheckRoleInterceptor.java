package com.empmall.interceptor;

import com.empmall.utils.CurrentHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class CheckRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 從 ThreadLocal 獲取當前用戶的角色
        Integer role = CurrentHolder.getCurrentRole();
        Integer currentId = CurrentHolder.getCurrentId();

        // 2. 判斷權限
        // 情況 A，如果是管理員 (Role=1)，擁有最高權限 -> 放行
        if (role != null && role == 1) {
            return true; // 是管理員，放行
        }

        // 情況 B，如果是普通員工 (Role=2)
        if (role != null && role == 2) {
            String requestURI = request.getRequestURI(); // 例如: /emps/10

            //放行規則 1: 查詢自己的資料 (例如 /emps/10)
            if (requestURI.equals("/emps/" + currentId)) {
                return true;
            }

            // 放行規則 2: 修改個人資料
            // 因為 UpdatePersonal 接口不帶 ID 參數，而是透過 Token 識別，所以直接比對路徑即可
            if (requestURI.contains("/emps/updatePersonal")) {
                return true;
            }
        }

        // 3. 權限不足的處理
        log.warn("權限不足！當前用戶Role: {}，嘗試訪問管理接口: {}", role, request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 返回 403 Forbidden
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\": 0, \"msg\": \"您的權限不足，無法執行此操作\"}");

        return false; // 攔截請求，不讓它進入 Controller
    }
}