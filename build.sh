#!/bin/bash

# 构建Docker镜像脚本
# 支持推送到镜像仓库

set -e

# 配置变量
IMAGE_NAME="ai-chat-monolith"
TAG=${1:-"latest"}
REGISTRY=${2:-""}  # 可选：镜像仓库地址

echo "开始构建Docker镜像..."

# 1. 构建Java项目
echo "构建Java项目..."
cd ai-chat-web
mvn clean package -DskipTests
cd ..

# 2. 构建Docker镜像
echo "构建Docker镜像: ${IMAGE_NAME}:${TAG}"
docker build -t ${IMAGE_NAME}:${TAG} .

# 3. 如果指定了镜像仓库，则推送
if [ ! -z "$REGISTRY" ]; then
    echo "推送镜像到仓库: ${REGISTRY}/${IMAGE_NAME}:${TAG}"
    docker tag ${IMAGE_NAME}:${TAG} ${REGISTRY}/${IMAGE_NAME}:${TAG}
    docker push ${REGISTRY}/${IMAGE_NAME}:${TAG}
fi

echo "构建完成！"
echo "镜像名称: ${IMAGE_NAME}:${TAG}"
echo "使用方法:"
echo "  本地使用: docker-compose up"
echo "  推送到仓库: ./build.sh latest your-registry.com"
