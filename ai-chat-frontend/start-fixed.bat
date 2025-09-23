@echo off
echo Starting AiChat Services (Unified Proxy Architecture)...
echo.

REM 定义Java启动参数
set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED

REM 启动后端服务 (Spring Boot API)
echo Starting Backend API service on port 8031...
if exist "ai-chat-1.1.jar" (
    start "Backend API (8031)" cmd /k "java %JAVA_OPTS% -jar ai-chat-1.1.jar"
    echo Backend API service started on port 8031
    echo Waiting 10 seconds for backend to start...
    timeout /t 10 /nobreak >nul
) else (
    echo ERROR: ai-chat-1.1.jar not found
)
echo.

REM 启动静态资源服务
echo Starting Static Resource service...
if exist "ai-static-1.1.jar" (
    start "Static Resources" cmd /k "java %JAVA_OPTS% -jar ai-static-1.1.jar"
    echo Static resource service started
    echo Waiting 5 seconds for static service to start...
    timeout /t 5 /nobreak >nul
) else (
    echo ERROR: ai-static-1.1.jar not found
)
echo.

REM 启动前端服务 (Vue.js)
echo Starting Frontend service on port 3000...
if exist "aichat-frontend" (
    start "Frontend (3000)" cmd /k "cd /d aichat-frontend && pnpm install && pnpm run serve"
    echo Frontend service started on port 3000
    echo Waiting 10 seconds for frontend to start...
    timeout /t 10 /nobreak >nul
) else (
    echo ERROR: aichat-frontend directory not found
)
echo.

REM 启动统一代理服务 (Node.js Unified Proxy)
echo Starting Unified Proxy service on port 8080...
if exist "aichat-node-proxy" (
    start "Unified Proxy (8080)" cmd /k "cd /d aichat-node-proxy && npm install && node server.js"
    echo Unified Proxy service started on port 8080
) else (
    echo ERROR: aichat-node-proxy directory not found
)
echo.

echo ========================================
echo Service Ports:
echo - Frontend: http://localhost:3000
echo - Unified Proxy: http://localhost:8080 (对外服务)
echo - Backend API: http://localhost:8031
echo ========================================
echo.
echo 花生壳映射配置：
echo 外网域名 → 本地IP:8080
echo.
echo All services startup commands sent!
echo Check each window for any errors.
echo.
echo Press any key to exit...
pause
