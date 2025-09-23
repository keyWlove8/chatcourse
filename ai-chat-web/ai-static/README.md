# AI-Static 模块

## 功能说明

`ai-static` 是一个专门用于处理图片上传和存储的静态文件服务模块，主要服务于 `ai-chat` 模块的图片上传需求。

## 核心功能

### 1. 图片上传
- **接口**: `POST /api/image/upload` 或 `POST /api/image/upload/direct`
- **功能**: 接收图片文件，生成唯一文件名，存储到本地，返回可访问的 URL
- **参数**: 
  - `file`: 图片文件（MultipartFile）
  - `category`: 分类（可选，默认为 "chat"）

### 2. 文件下载
- **接口**: `GET /download/**`
- **功能**: 根据存储路径准确找到并返回文件
- **特点**: 支持任意深度的路径访问

### 3. 文件名唯一性保证
- **格式**: `image_日期_时间戳_UUID.扩展名`
- **示例**: `image_20250829_143052_a1b2c3d4.jpg`
- **特点**: 每次上传都会生成唯一的文件名，避免冲突，且文件名前缀与文件夹结构完全对应

### 4. 文件存储结构
```
static-files/
├── images/
│   └── chat/
│       └── 2025/
│           └── 08/
│               └── 29/
│                   ├── image_20250829_143052_a1b2c3d4.jpg
│                   └── image_20250829_143055_e5f6g7h8.png
```

**说明**: 文件夹结构按年/月/日分层，文件名前缀也包含对应日期，便于文件管理和查找

## 配置说明

### 配置文件: `application.yaml`
```yaml
k8:
  static:
    basePath: ./static-files
    host: http://localhost:9000

server:
  port: 9000
  servlet:
    context-path: /
```

### 配置项说明
- **basePath**: 文件存储的基础路径，支持相对路径和绝对路径
- **host**: 文件服务器的访问地址，用于生成文件的下载链接

## 响应格式

### 上传成功响应
```json
{
  "uri": "http://localhost:9000/download/images/chat/2025/08/29/image_20250829_143052_a1b2c3d4.jpg",
  "status": "OK",
  "fileName": "image_20250829_143052_a1b2c3d4.jpg",
  "size": 1024000,
  "contentType": "image/jpeg",
  "storagePath": "images/chat/2025/08/29/image_20250829_143052_a1b2c3d4.jpg"
}
```

### 上传失败响应
```json
{
  "uri": "",
  "status": "FAIL",
  "fileName": "",
  "size": 0,
  "contentType": "",
  "storagePath": ""
}
```

## 使用流程

### 1. 图片上传流程
```
ai-chat 发送图片 → ai-static 接收文件 → 生成唯一文件名 → 保存到本地 → 返回访问 URL
```

### 2. 图片访问流程
```
前端请求图片 → ai-static 根据 URL 路径 → 找到本地文件 → 返回文件内容
```

## 部署说明

### 1. 本地开发
- 启动后文件会存储在 `./static-files` 目录（相对于 jar 包位置）
- 访问地址: `http://localhost:9000`

### 2. 生产环境
- 修改 `host` 配置为实际的公网地址
- 确保 `basePath` 指向有足够存储空间的目录
- 建议使用绝对路径，避免相对路径的歧义

## 注意事项

1. **文件唯一性**: 每次上传都会生成新的文件名，确保不会覆盖已有文件
2. **路径准确性**: 返回的 URL 路径与本地存储路径完全对应，确保能准确找到文件
3. **存储空间**: 需要确保存储目录有足够的空间
4. **权限管理**: 当前版本没有权限控制，生产环境建议添加访问控制

## 技术特点

- 使用 Spring Boot 3.1.12 框架（兼容 Java 17）
- 支持配置文件化
- 文件名包含日期、时间戳和 UUID，确保唯一性
- 支持任意文件扩展名
- 自动创建目录结构
- 全局异常处理
- **轻量级设计**: 只包含必要的依赖，无 Spring Security 等重量级组件

## 依赖说明

- **spring-boot-starter-web**: 提供 Web 服务能力
- **hutool-all**: 提供文件操作等工具方法
- **lombok**: 简化代码编写
