/**
 * 应用配置文件
 * 包含所有环境相关的配置项
 */

// 开发环境配置
const devConfig = {
  // 代理服务地址（开发环境直接连接后端）
  proxyUrl: 'http://localhost:8080'
}

// 生产环境配置
import prodConfig from './prod.js'

// 根据环境选择配置
const isDev = process.env.NODE_ENV === 'development'
const config = isDev ? devConfig : prodConfig

export default config
