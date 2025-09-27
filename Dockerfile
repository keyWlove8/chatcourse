# 多阶段构建 - 4个服务打包成1个镜像
FROM node:18-alpine as frontend-builder

# 构建前端
WORKDIR /app/frontend
COPY ai-chat-frontend/aichat-frontend/package.json ai-chat-frontend/aichat-frontend/pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

COPY ai-chat-frontend/aichat-frontend/ .
RUN pnpm run build

# 构建Node.js代理
FROM node:18-alpine as proxy-builder

WORKDIR /app/proxy
COPY ai-chat-frontend/aichat-node-proxy/package.json ai-chat-frontend/aichat-node-proxy/pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

COPY ai-chat-frontend/aichat-node-proxy/ .

# 构建Java应用
FROM maven:3.8-openjdk-21 as java-builder

WORKDIR /app
COPY ai-chat-web/pom.xml .
COPY ai-chat-web/ai-chat/pom.xml ai-chat-web/ai-chat/
COPY ai-chat-web/ai-static/pom.xml ai-chat-web/ai-static/
COPY ai-chat-web/ai-common/pom.xml ai-chat-web/ai-common/

# 下载依赖
RUN mvn dependency:go-offline -f ai-chat-web/pom.xml

# 复制源代码
COPY ai-chat-web/ .
RUN mvn clean package -DskipTests

# 最终镜像
FROM openjdk:21-jdk-slim

# 安装Node.js
RUN apt-get update && \
    apt-get install -y nodejs npm && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制Java应用
COPY --from=java-builder /app/ai-chat/target/ai-chat-*.jar ai-chat.jar
COPY --from=java-builder /app/ai-static/target/ai-static-*.jar ai-static.jar

# 复制前端文件
COPY --from=frontend-builder /app/frontend/dist /app/frontend

# 复制Node.js代理
COPY --from=proxy-builder /app/proxy /app/proxy
WORKDIR /app/proxy
RUN npm install

# 创建启动脚本
WORKDIR /app
COPY start-all-services.sh .
RUN chmod +x start-all-services.sh

# 设置环境变量
ENV PUBLIC_HOST=localhost:8080

# 创建静态文件目录
RUN mkdir -p /app/static-files

# 暴露端口
EXPOSE 8080

# 启动所有服务
CMD ["./start-all-services.sh"]
