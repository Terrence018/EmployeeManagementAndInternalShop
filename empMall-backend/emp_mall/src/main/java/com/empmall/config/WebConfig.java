package com.empmall.config;

import com.empmall.interceptor.CheckRoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置類
 * 用於註冊權限攔截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CheckRoleInterceptor checkRoleInterceptor;

    //管理員專用路徑（Role=1)
    private static final String[] ADMIN_PATHS = {
            "/emps/**",             // 員工管理
            "/dept/**",             // 部門管理
            "/products/**",         // 商品後台管理
            "/orders/**",           // 訂單後台管理
            "/report/**",           // 報表統計
            "/log/**",              // 日誌紀錄
            "/points/manage",       // 點數發放
            "/points/overview"      // 員工點數查看 (管理員用)
    };

    private static final String[] PUBLIC_PATHS = {
            "/login",               // 登入
            "/emps/login",          // 登入
            "/emps/info",           // 員工查詢自己資料
            "/emps/update_personal",    //員工修改自己資料
            "/ws-endpoint/**", //排除 WebSocket 路徑

            //--- 商城相關放行 ---
            "/products",            // 放行「獲取商品列表」(員工商城頁面用)
            "/orders/exchange",     // 放行「兌換商品」接口
            "/orders/my/**",        // 放行「查詢我的訂單」
            "/points/history/**",   // 放行「查詢個人點數紀錄」
            "/orders/batchExchange",    //放行批量結帳接口
            "/email/send",          //排除發信接口
            "/emps/reset-password"  //排除重置密碼接口
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 註冊「角色檢查攔截器」
        registry.addInterceptor(checkRoleInterceptor)
                .addPathPatterns(ADMIN_PATHS)
                .excludePathPatterns(PUBLIC_PATHS);
    }
}