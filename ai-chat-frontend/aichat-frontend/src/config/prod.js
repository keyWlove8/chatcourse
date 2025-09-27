/**
 * 生产环境配置 - 单体Docker镜像
 * 所有服务都在同一个容器内，通过localhost通信
 * 支持环境变量配置
 */

const prodConfig = {
  // 代理服务地址（支持环境变量）
  // 可以通过环境变量 PUBLIC_HOST 配置
  // 例如: https://yourdomain.com 或 http://123.456.789.123:8080
  proxyUrl: process.env.PUBLIC_HOST ? `http://${process.env.PUBLIC_HOST}` : 'http://localhost:8080'
}

export default prodConfig
