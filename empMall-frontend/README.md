# 員工管理與內部商城系統 (前端)

這本專案的前端部分，基於 Vue 3 + Vite 構建，旨在提供高效的員工管理與便捷的內部商城體驗。系統整合了數據視覺化、地圖定位以及即時通訊功能。

## 🛠️ 技術棧 (Tech Stack)

本專案採用以下關鍵技術進行開發：

* **核心框架**: [Vue 3](https://vuejs.org/)
* **建構工具**: [Vite 3](https://vitejs.dev/)
* **UI 組件庫**: [Element Plus](https://element-plus.org/) (配合 Icon 圖標庫)
* **路由管理**: Vue Router 4
* **HTTP 請求**: Axios
* **數據視覺化**: Chart.js / Vue-Chartjs (用於後台數據統計)
* **地圖功能**: Vue3 Google Map (用於員工或物流定位)
* **即時通訊**: SockJS + StompJS (用於員工與管理員的 WebSocket 聊天)

## 🚀 快速開始 (Quick Start)

請按照以下步驟在本地端啟動專案。

### 1. 環境準備
請確保您的電腦已安裝 [Node.js](https://nodejs.org/) (建議 LTS 版本)。

### 2. 安裝依賴
進入前端專案目錄並安裝所需的 npm 套件：

```bash
cd empMall-frontend
npm install
```
### 3. 啟動開發服務器
安裝完成後，執行以下指令開啟網頁：

```bash
npm run dev
```

啟動後，終端機將顯示訪問地址（通常為 http://localhost:5173 或 http://localhost:4173，視 Vite 配置而定）。


## 📦構建與部署 (Build)

當專案開發完成準備部署時，請執行：

```bash
npm run build
```

```bash
npm run preview
```

## 🔑 測試帳號 (Demo Accounts)

為了方便體驗系統功能，系統預設了不同權限的測試帳號：

| 角色 (Role) | 帳號 (Username) | 密碼 (Password) | 權限與功能說明 |
|------------|----------------|----------------|---------------|
| 系統管理員 | admin          | 123456         | 全系統權限・員工帳號管理・商品上下架管理・查看銷售數據圖表 (Chart.js)・接收並回覆員工客服請求等 |
| 一般員工   | user222        | 123456         | 沈喬莉（行銷專員）・訪問內部商城與購物・查看地圖資訊・發起 WebSocket 客服聊天等 |

