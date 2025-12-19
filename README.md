# 員工管理與內部商城系統 (Employee Management & Internal Mall System)

## 📖 專案簡介
這是一個基於 **SpringBoot** 與 **Vue.js** 開發的前後端分離系統，為職訓局 Java 班的畢業專題作品。
系統主要功能包含企業內部的員工資訊管理（CRUD）、權限控管（RBAC），以及一個完整的內部點數兌換商城，模擬企業福利系統的運作流程。

## 🛠️ 技術棧 (Tech Stack)

### Backend (後端)
* **核心框架:** Spring Boot 3.x
* **資料庫:** MySQL 8.0
* **ORM:** Mybatis / Mybatis-Plus (依你實際情況調整)
* **安全與權限:** JWT (JSON Web Token), Filter, Interceptor
* **工具:** Maven, Lombok, DataGrip

### Frontend (前端)
* **框架:** Vue.js
* **HTTP 請求:** Axios
* **UI 組件庫:** Element UI (或是你用的其他庫)

## ✨ 核心功能 (Key Features)

* **👥 員工管理系統**
    * 完整的員工資料增刪改查 (CRUD)。
    * **RBAC 權限設計**：區分管理員與一般員工，不同角色擁有不同操作權限。
    * **雙重日誌記錄**：實作 `System Log` 與 `Business Log`，完整記錄操作軌跡與業務變動。

* **🛒 內部商城系統**
    * **商品管理**：管理員可上下架商品、調整庫存與點數價格。
    * **購物車與下單**：完整的電商購物流程體驗。
    * **訂單管理**：追蹤訂單狀態（待處理、已完成、取消）。

* **🔒 安全機制**
    * 登入驗證與 Token 管理。
    * 敏感操作（如刪除員工）需進行 Email 驗證 (依你之前描述)。

## 🚀 快速啟動 (Quick Start)

### 1. 資料庫設定
本專案附帶完整的 SQL 腳本。
1. 進入 `empMall-backend/sql/` 資料夾。
2. 將 `empmall.sql` 匯入您的 MySQL 資料庫。
3. 修改後端 `application.yml` 中的資料庫帳號密碼。

### 2. 後端啟動 (Backend)
```bash
cd empMall-backend
# 建議使用 IDEA 開啟並執行 Main Application
