# AI聊天系统部署指南

## 📋 项目概述

AI聊天系统包含4个服务，打包成1个Docker镜像：
- **前端服务** (Vue.js + pnpm serve)
- **后端服务** (Spring Boot AI聊天服务)
- **静态文件服务** (Spring Boot 文件服务)
- **代理服务** (Node.js 统一入口)

## 🛠️ 开发环境

### 本地开发步骤

1. **启动后端服务** (在IDEA中)
   - 启动 `AiServer.java` (端口8031)
   - 启动 `StaticServer.java` (端口9000)

2. **启动前端服务**
   ```bash
   cd ai-chat-frontend/aichat-frontend
   pnpm serve
   ```

3. **启动代理服务**
   ```bash
   cd ai-chat-frontend/aichat-node-proxy
   node server.js
   ```

4. **访问应用**
   - 前端: http://localhost:3000
   - 代理入口: http://localhost:8080

## 🚀 生产环境部署

### 方案一：本地构建镜像

1. **构建镜像**
   ```bash
   ./build.sh latest
   ```

2. **配置环境变量**
   ```bash
   cp env.example .env
   # 编辑 .env 文件，设置你的配置
   ```

3. **启动服务**
   ```bash
   docker-compose up -d
   ```

### 方案二：远程拉取镜像

1. **配置环境变量**
   ```bash
   cp env.example .env
   # 编辑 .env 文件，设置：
   # IMAGE_NAME=your-registry.com/ai-chat-monolith
   # TAG=latest
   # PUBLIC_HOST=yourdomain.com:8080
   ```

2. **启动服务**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

## ⚙️ 环境变量配置

### 必须配置
```bash
PUBLIC_HOST=yourdomain.com:8080          # 公网访问地址
MYSQL_ROOT_PASSWORD=your_password         # 数据库密码
```

### 可选配置
```bash
MYSQL_DATABASE=ai_chat                   # 数据库名 (默认: ai_chat)
MYSQL_USER=ai_user                       # 数据库用户 (默认: ai_user)
MYSQL_PASSWORD=ai_password               # 数据库密码 (默认: ai_password)
IMAGE_NAME=ai-chat-monolith              # 镜像名 (默认: ai-chat-monolith)
TAG=latest                               # 镜像标签 (默认: latest)
```

## 📁 文件说明

```
chat2project/
├── docker-compose.yml          # 本地构建镜像配置
├── docker-compose.prod.yml     # 远程拉取镜像配置
├── env.example                 # 环境变量配置示例
├── build.sh                    # 镜像构建脚本
├── Dockerfile                  # 镜像构建文件
└── start-all-services.sh       # 容器内启动脚本
```

## 🔧 常用命令

### 查看服务状态
```bash
docker-compose ps
```

### 查看服务日志
```bash
docker-compose logs -f
docker-compose logs -f ai-chat-app
```

### 重启服务
```bash
docker-compose restart
docker-compose restart ai-chat-app
```

### 停止服务
```bash
docker-compose down
```

### 进入容器
```bash
docker exec -it ai-chat-app /bin/bash
```

## 🌐 访问地址

部署成功后，通过以下地址访问：
- **应用入口**: `http://你的公网IP:8080`
- **API接口**: `http://你的公网IP:8080/api/...`
- **静态文件**: `http://你的公网IP:8080/static/...`

## ⚠️ 注意事项

1. **端口开放**: 确保服务器防火墙开放8080端口
2. **Java版本**: 确保服务器有Java 17+
3. **Docker版本**: 确保Docker和Docker Compose已安装
4. **内存要求**: 建议至少2GB内存
5. **磁盘空间**: 建议至少5GB可用空间

## 🆘 故障排除

### 检查服务状态
```bash
# 检查容器状态
docker-compose ps

# 检查端口监听
netstat -tlnp | grep 8080

# 检查服务日志
docker-compose logs ai-chat-app
```

### 常见问题
1. **端口被占用**: 检查8080端口是否被其他服务占用
2. **数据库连接失败**: 检查MySQL服务是否正常启动
3. **前端无法访问**: 检查代理服务是否正常运行
4. **镜像拉取失败**: 检查网络连接和镜像仓库地址

## 📞 技术支持

如有问题，请检查：
1. 环境变量配置是否正确
2. 端口是否被占用
3. 服务日志中的错误信息
4. 网络连接是否正常
