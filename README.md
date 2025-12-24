# 員工管理與內部商城系統 (Employee Management & Internal Mall System)

## 📖 專案簡介
這是一個模擬企業內部福利運作的 **全端前後端分離系統**。
除了基礎的員工資訊管理（CRUD）與 RBAC 權限控管外，更使用了Interceptor+Filter雙重攔截驗證以確保數據安全。，本專案最大的亮點在於整合了 **WebSocket 即時通訊功能**，實現了員工與管理員之間的線上客服系統，並結合完整的 **點數商城** 與 **數據可視化** 儀表板。

## 🛠️ 技術棧 (Tech Stack)

### Backend (後端)
* **核心框架:** Spring Boot 3.5.7
* **資料庫:** MySQL 8.0
* **ORM:** Mybatis
* **即時通訊:** **WebSocket (STOMP + SockJS)**
* **安全驗證:** JWT (JSON Web Token), Filter, Interceptor
* **第三方服務:** **JavaMailSender (Gmail SMTP)**
* **工具:** Maven, Lombok, DataGrip, IntellJ IDEA

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
   * **第一層 Filter (過濾器)**：處理跨域請求 (CORS) 與初步的請求過濾。
   * **第二層 Interceptor (攔截器)**：深入 Spring Context，驗證 JWT Token 有效性並攔截越權操作，防止 API 被惡意調用。
* **個人中心**：員工可查看積分歷史、修改個人資料（含 Email 驗證碼機制）。

### 4. 📊 數據統計與日誌
* **儀表板**：圖表化顯示銷售趨勢與部門分佈。
* **雙重日誌**：系統自動記錄關鍵操作 (Operate Log) 與業務(員工、商城)變更日誌，方便管理者隨時檢閱。

## 📂 專案結構 (Project Structure)

```text
/ (Root)
├── empMall-backend/        # 後端專案根目錄
│   ├── sql/                # 資料庫初始化腳本 (含結構與預設數據)
│   └── emp_mall/           # SpringBoot 原始碼 (Maven Project)
└── empMall-frontend/       # 前端 Vue3 原始碼 (Vite Project)
```

## 快速啟動 (Quick Start)

### Step 1: 資料庫設置 (Database)
1. 請確保本地已安裝 MySQL 8.0。

2. 建立一個新的資料庫（例如命名為 tlias 或參考 application.yml 設定）。
 
3. 執行 empMall-backend/sql/ 目錄下的 SQL 腳本，完成資料表結構與預設數據的匯入。

### Step 2: 啟動後端 (Backend)
1. 使用 IntelliJ IDEA 開啟 empMall-backend 目錄。

2. 等待 Maven 下載依賴完成。

3. 修改 application.yml 中的資料庫帳號密碼。

4. 執行 Main 方法啟動 Spring Boot 應用 (預設 Port: 8080)。

### Step 3: 啟動前端 (Frontend)
前端有獨立的詳細說明文件，請進入目錄操作：

```bash
cd empMall-frontend
npm install
npm run dev
```

