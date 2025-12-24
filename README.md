# 員工管理與內部商城系統 (Employee Management & Internal Mall System)

## 📖 專案簡介
這是一個模擬企業內部福利運作的 **全端前後端分離系統**。別名 **EMP Mall**。
除了基礎的員工資訊管理（CRUD）與 商品管理（CRUD)外，更使用了 **Interceptor** + **Filter** 雙重攔截驗證，以達成 RBAC權限控管 確保數據安全。本專案整合了 **WebSocket 即時通訊功能**，實現了員工與管理員之間的線上客服系統，且結合 **點數商城** 與 **數據可視化** 儀表板 以及 **日誌記錄**，為一個功能完整的 SpringBoot + Vue + MySQL 專案。

## 🛠️ 技術棧 (Tech Stack)

### Backend (後端)
* **核心框架:** Spring Boot 3.5.7
* **資料庫:** MySQL 8.0
* **ORM:** Mybatis
* **即時通訊:** **WebSocket (STOMP + SockJS)**
* **安全驗證:** JWT (JSON Web Token), Filter, Interceptor
* **雲端與第三方服務:** **AWS S3(儲存圖片)** 、 **JavaMailSender (Gmail SMTP)**
* **工具:** Maven, Lombok, DataGrip, IntelliJ IDEA

### Frontend (前端)
* **核心框架:** Vue 3 (Composition API) + Vite
* **UI 組件庫:** Element Plus
* **HTTP 請求:** Axios (封裝攔截器)
* **即時通訊:** `stompjs` + `sockjs-client`
* **地圖與圖表:** **Vue3 Google Map**, Chart.js

## ✨ 核心功能 (Key Features)

### 1. 💬 即時客服聊天室 (WebSocket)
* **雙向即時通訊**：基於 STOMP 協議，實現員工與管理員的一對一對話。
* **狀態管理**：
  * 員工可發起工單（選擇問題類型）。
  * 管理員接收即時通知（Notification）並決定是否接單。

### 2. 🛒 內部福利商城
* **商品管理**：管理員可上下架商品、上傳圖片、調整庫存與點數定價。
* **購物流程**：員工使用個人積分進行兌換，支援購物車操作與訂單追蹤。
* **庫存並發控制**：確保商品扣庫存的準確性。

### 3. 👥 員工與權限管理 (RBAC)
* **權限分級**：嚴格區分 **管理員 (Role 1)** 與 **一般員工 (Role 2)** 的操作介面與 API 訪問權限。
* **雙重資安防護**：
   * **第一層 Filter (過濾器)**：處理跨域請求 (CORS)、JWT Token 解析與有效性驗證，並將使用者資訊存入 ThreadLocal。
   * **第二層 Interceptor (攔截器)**：深入 Spring Context，基於 ThreadLocal 中的角色資訊進行 RBAC 權限控管，攔截越權操作。
* **個人中心**：員工可查看積分歷史、修改個人資料（含 Email 驗證碼機制）。

### 4. 📊 數據統計與日誌記錄
* **儀表板**：圖表化顯示銷售趨勢與部門分佈。
* **雙重日誌**：系統自動記錄關鍵操作 (Operate Log) 與業務(員工Emp Log、商城Product Log)日誌，方便管理者隨時檢閱。

## 📂 專案結構 (Project Structure)

```text
/ (Root)
├── empMall-backend/        # 後端專案根目錄
│   ├── sql/                # 資料庫初始化腳本 (含結構與預設數據)
│   └── emp_mall/           # SpringBoot 原始碼 (Maven Project)
└── empMall-frontend/       # 前端 Vue3 原始碼 (Vite Project)

/後端
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

前端
empMall-frontend/
├── src/
│   ├── api/                # 後端 API 串接管理 (Axios)
│   ├── assets/             # 靜態資源 (Images等）
│   ├── components/         # 共用組件 (Global Components)
│   ├── router/             # 路由配置 (Vue Router)
│   ├── utils/              # 工具函式 
│   ├── views/              # 頁面級組件 ，按業務功能分類
│   │   ├── layout/         # 佈局 
│   │   ├── login/          # 登入頁
│   │   ├── emp/ & dept/    # 人事管理模組
│   │   ├── mall/ & order/  # 商城與訂單模組
│   │   └── stats/          # 數據統計模組
│   ├── App.vue             # 根組件
│   └── main.js             # 入口文件
├── package.json            # 專案依賴管理
└── vite.config.js          # Vite 設定檔

```

## 快速啟動 (Quick Start)

### Step 1: 資料庫設置 (Database)
1. 請確保本地已安裝 MySQL 8.0。

2. 建立一個新的資料庫（例如命名為 emp_mall 或參考 application.yml 設定）。
 
3. 執行 empMall-backend/sql/ 目錄下的 SQL 腳本，完成資料表結構與預設數據的匯入。

### Step 2: 啟動後端 (Backend)
1. 使用 IntelliJ IDEA 開啟 empMall-backend/emp_mall 目錄。

2. 等待 Maven 下載依賴完成。

3. **重要**：修改 application.yml 中的配置：

   * 資料庫帳號密碼 (Datasource)
   * AWS S3 Access Keys (若無可跳過，但圖片上傳功能將受限)
   * Gmail SMTP 密碼 (若無可跳過，但郵件驗證碼功能將受限)

4. 執行 啟動類的 Main 方法啟動 Spring Boot 應用 (預設 Port: 8080)。

### Step 3: 啟動前端 (Frontend)
前端有獨立的詳細說明文件，請進入目錄操作：

重要設定： 在 empMall-frontend 根目錄下建立 .env 檔案，並設定 Google Maps API Key（詳細格式請見前端 README）。

```bash

cd empMall-frontend

npm install

npm run dev
```

