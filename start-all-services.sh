#!/bin/bash

# 简化启动脚本 - 4个服务在1个容器内
# 支持环境变量配置

# 获取环境变量
PUBLIC_HOST=${PUBLIC_HOST:-"localhost:8080"}
echo "使用公网地址: $PUBLIC_HOST"
echo "开始启动AI聊天系统..."

# 1. 启动Java服务
echo "启动AI聊天服务..."
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -Dhttp.proxyHost= \
     -Dhttp.proxyPort= \
     -Dhttps.proxyHost= \
     -Dhttps.proxyPort= \
     -Dhttp.nonProxyHosts="*" \
     -Dhttps.nonProxyHosts="*" \
     -jar ai-chat.jar --spring.profiles.active=prod &
AI_CHAT_PID=$!

echo "启动静态文件服务..."
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -Dhttp.proxyHost= \
     -Dhttp.proxyPort= \
     -Dhttps.proxyHost= \
     -Dhttps.proxyPort= \
     -Dhttp.nonProxyHosts="*" \
     -Dhttps.nonProxyHosts="*" \
     -jar ai-static.jar --spring.profiles.active=prod &
STATIC_PID=$!

# 2. 等待Java服务启动
echo "等待Java服务启动..."
sleep 30

# 3. 启动前端服务（使用构建后的静态文件）
echo "启动前端服务..."
cd /app/frontend
# 使用简单的HTTP服务器服务静态文件
npx serve -s . -l 3000 &
FRONTEND_PID=$!

# 4. 等待前端服务启动
sleep 5

# 5. 启动代理服务
echo "启动代理服务..."
cd /app/proxy
node server.js &
PROXY_PID=$!

# 6. 等待代理服务启动
sleep 5

echo "所有服务已启动完成！"
echo "访问地址: http://localhost:8080"
echo "服务状态:"
echo "  - AI聊天服务: PID $AI_CHAT_PID (端口8031)"
echo "  - 静态文件服务: PID $STATIC_PID (端口9000)"
echo "  - 前端服务: PID $FRONTEND_PID (端口3000)"
echo "  - 代理服务: PID $PROXY_PID (端口8080)"

# 7. 监控服务状态
while true; do
    # 检查Java服务是否还在运行
    if ! kill -0 $AI_CHAT_PID 2>/dev/null; then
        echo "AI聊天服务已停止，重启中..."
        java --add-opens java.base/java.lang=ALL-UNNAMED \
             --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
             --add-opens java.base/java.util=ALL-UNNAMED \
             -Dhttp.proxyHost= \
             -Dhttp.proxyPort= \
             -Dhttps.proxyHost= \
             -Dhttps.proxyPort= \
             -Dhttp.nonProxyHosts="*" \
             -Dhttps.nonProxyHosts="*" \
             -jar ai-chat.jar --spring.profiles.active=prod &
        AI_CHAT_PID=$!
    fi
    
    if ! kill -0 $STATIC_PID 2>/dev/null; then
        echo "静态文件服务已停止，重启中..."
        java --add-opens java.base/java.lang=ALL-UNNAMED \
             --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
             --add-opens java.base/java.util=ALL-UNNAMED \
             -Dhttp.proxyHost= \
             -Dhttp.proxyPort= \
             -Dhttps.proxyHost= \
             -Dhttps.proxyPort= \
             -Dhttp.nonProxyHosts="*" \
             -Dhttps.nonProxyHosts="*" \
             -jar ai-static.jar --spring.profiles.active=prod &
        STATIC_PID=$!
    fi
    
    # 检查Node.js服务是否还在运行
    if ! kill -0 $PROXY_PID 2>/dev/null; then
        echo "代理服务已停止，重启中..."
        cd /app/proxy
        node server.js &
        PROXY_PID=$!
    fi
    
    sleep 10
done
