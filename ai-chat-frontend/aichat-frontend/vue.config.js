module.exports = {
  // 开发环境服务器配置
  devServer: {
    port: 3000, // 前端直接运行在3000端口
    host: '0.0.0.0', // 允许所有IP访问
    allowedHosts: 'all', // 允许所有Host头
    // 配置静态文件服务
    static: {
      directory: './public',
      publicPath: '/'
    }
  },
  lintOnSave: false, // 关闭 ESLint 实时检查（避免干扰开发）
  
  // 确保静态文件路径正确
  publicPath: '/',
  assetsDir: 'static'
}