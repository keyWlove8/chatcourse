/**
 * 应用配置文件
 * 包含所有环境相关的配置项
 */

// 开发环境配置
const devConfig = {
  // 代理服务地址（统一入口，处理API和静态文件）
  proxyUrl: 'http://localhost:8080'
}

// 生产环境配置
const prodConfig = {
  // 代理服务地址（生产环境需要修改为实际代理地址）
  proxyUrl: 'http://localhost:8080'
}

// 根据环境选择配置
const isDev = process.env.NODE_ENV === 'development'
const config = isDev ? devConfig : prodConfig

export default config
