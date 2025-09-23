module.exports = {
  // 开发服务器配置
  devServer: {
    port: 8080, // 固定端口
    open: true, // 自动打开浏览器
    proxy: {
      // 代理后端 API，解决跨域
      '/api': {
        target: 'http://localhost:8081', // 替换为你的后端服务地址（如 Spring Boot 端口）
        changeOrigin: true,
        pathRewrite: { '^/api': '' } // 去掉请求前缀 /api
      }
    }
  },
  // 关闭 eslint 校验（可选，避免开发时报错）
  lintOnSave: false
}