# 員工管理與內部商城系統 - 後端服務 (Backend API)

本專案為系統的核心後端服務，基於 **Spring Boot 3.5.7** 構建，提供 RESTful API 供前端調用，並透過 **WebSocket** 處理即時通訊需求。

## 技術細節 (Technical Details)

* **JDK 版本**: JDK 17
* **核心框架**: Spring Boot 3.5.7
* **資料庫**: MySQL 8.0
* **ORM 框架**: MyBatis
* **連接池**: HikariCP
* **即時通訊**: Spring WebSocket (STOMP 協議)
* **安全驗證**: JWT + Interceptor + Filter
* **雲端與第三方服務:** AWS S3(儲存圖片)、JavaMailSender (Gmail SMTP)
* **工具庫**: Lombok

## 專案結構說明 (Package Structure)

代碼遵循標準的分層架構 (Layered Architecture)：

```text
com.empmall
├── anno/               # 自定義註解 (如 @Log 用於日誌記錄)
├── aop/                # AOP 切面 (處理操作日誌記錄 LogAspect)
├── config/             # 配置類 (WebSocketConfig, WebConfig)
├── controller/         # 控制層 (處理 HTTP 請求與 WebSocket 訊息)
├── exception/          # 全局異常處理 (GlobalExceptionHandler)
├── filter/             # 過濾器 (處理跨域 CORS, 登入校驗 Filter)
├── interceptor/        # 攔截器 (JWT 權限驗證 Interceptor)
├── mapper/             # 持久層 (MyBatis Interface)
├── pojo/               # 實體類 (Entity, DTO, VO)
├── service/            # 業務邏輯層 (事務控制, 複雜邏輯)
├── utils/              # 工具類 (JwtUtils, CurrentHolder、UploadFileUtils)
└── EmpMallWebManagementApplication  # 啟動類
```

## 環境配置與啟動 (Setup & Run)
### 1. 資料庫準備
   在使用本專案前，請務必先導入資料庫結構與數據。

SQL 腳本位置: ../sql/empmall.sql (請依據實際檔名調整)

資料庫名稱: emp_mall 

### 2. 修改配置文件
   請開啟 src/main/resources/application.yml (或 .properties)，並確認以下設定與你的本地環境一致：

```YML
spring:
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/
username: root        # 修改為你的 MySQL 帳號
password: your_password # 修改為你的 MySQL 密碼

mybatis:
mapper-locations: classpath:mapper/*.xml
configuration:
map-underscore-to-camel-case: true # 開啟駝峰命名映射
```

### 3. 啟動專案
使用 IDE: 找到 EmpMallApplication.java，點擊 Run。

使用 Maven:
```Bash
mvn spring-boot:run
```
## 核心功能與端點 (Endpoints)

### 認證機制 (Authentication)
本系統採取 雙重防護機制：

1. Filter (過濾器)：處理跨域請求 (CORS)、JWT Token 解析與有效性驗證，並將使用者資訊存入 ThreadLocal。

2. Interceptor（攔截器）：深入 Spring Context，基於 ThreadLocal 中的角色資訊進行 RBAC 權限控管，攔截越權操作。

### WebSocket 配置
Endpoint: /ws (前端連線點)

協議: STOMP

訂閱路徑 (Subscribe): /topic/..., /queue/...

發送路徑 (Publish): /app/...

### 打包與部署 (Build)

```Bash
mvn clean package -DskipTests
```
打包完成後，檔案將位於 target/emp-mall-0.0.1-SNAPSHOT.jar。

執行 JAR 檔：
```Bash
java -jar target/emp-mall-0.0.1-SNAPSHOT.jar
```
