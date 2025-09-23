module.exports = {
  // 开发环境服务器配置
  devServer: {
    port: 3000, // 前端直接运行在3000端口
    host: '0.0.0.0', // 允许所有IP访问
    allowedHosts: 'all', // 允许所有Host头
    client: {
      webSocketURL: 'auto://0.0.0.0:0/ws' // WebSocket配置
    }
    // 移除代理配置，因为现在有统一代理服务
  },
  lintOnSave: false // 关闭 ESLint 实时检查（避免干扰开发）
}