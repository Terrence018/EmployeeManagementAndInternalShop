# --- 第一階段：建置環境 (Node.js) ---
FROM node:18-alpine as build-stage

WORKDIR /app

# 複製 package.json
COPY package*.json ./

# 安裝依賴
RUN npm install

# 複製所有程式碼
COPY . .

# 開始打包 (會產生 dist 資料夾)
RUN npm run build

# --- 第二階段：生產環境 (Nginx) ---
FROM nginx:stable-alpine as production-stage

# 複製我們剛剛寫好的 nginx 設定檔
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 從第一階段把打包好的檔案複製過去
COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]