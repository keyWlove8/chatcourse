// 存储 Token
export const setToken = (accessToken, refreshToken, tokenType = 'Bearer', expiresIn = 3600) => {
  localStorage.setItem('accessToken', accessToken)
  localStorage.setItem('refreshToken', refreshToken)
  localStorage.setItem('tokenType', tokenType)
  // 存储过期时间（使用后端返回的expiresIn，单位：秒）
  localStorage.setItem('tokenExpire', Date.now() + expiresIn * 1000)
}

// 获取 AccessToken
export const getAccessToken = () => {
  return localStorage.getItem('accessToken') || ''
}

// 获取 RefreshToken
export const getRefreshToken = () => {
  return localStorage.getItem('refreshToken') || ''
}

// 获取 Token 类型
export const getTokenType = () => {
  return localStorage.getItem('tokenType') || 'Bearer'
}

// 检查 Token 是否过期
export const isTokenExpired = () => {
  const expireTime = localStorage.getItem('tokenExpire')
  return !expireTime || Date.now() > expireTime
}

// 清除 Token
export const removeToken = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('tokenType')
  localStorage.removeItem('tokenExpire')
}