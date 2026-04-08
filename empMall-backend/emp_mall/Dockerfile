# 使用 Eclipse Temurin 的 Java 21 (推薦，業界主流)
# alpine 版本極小，支援 Mac M4 (ARM64)
FROM eclipse-temurin:21-jdk-alpine

# 設定容器內的工作目錄
WORKDIR /app

# 複製編譯好的 jar 檔
# 假設你的 jar 檔在 target/ 下
COPY target/*.jar app.jar

# 暴露 8080 埠
EXPOSE 8080

# 啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]