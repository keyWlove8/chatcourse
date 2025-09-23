// 认证相关配置
export const AUTH_CONFIG = {
  // Token 配置
  TOKEN: {
    TYPE: 'Bearer',           // 默认token类型
    ACCESS_TOKEN_KEY: 'accessToken',
    REFRESH_TOKEN_KEY: 'refreshToken',
    TOKEN_TYPE_KEY: 'tokenType',
    EXPIRE_KEY: 'tokenExpire',
    DEFAULT_EXPIRE: 3600      // 默认过期时间（秒）
  },
  
  // 权限配置
  PERMISSIONS: {
    // 知识库管理权限
    KNOWLEDGE: {
      CREATE: 'isAdmin',      // 创建知识库需要管理员权限
      UPLOAD: 'isAdmin',      // 上传文档需要管理员权限
      VIEW: true              // 查看知识库所有用户都可以
    }
  },
  
  // API 路径配置
  API: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    CURRENT_USER: '/auth/currentUser'
  }
}
