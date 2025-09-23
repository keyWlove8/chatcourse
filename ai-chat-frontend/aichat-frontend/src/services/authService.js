import request from './api'

// 登录
export const login = (data) => {
  return request.post('/auth/login', data) // 后端接口：/api/auth/login
}

// 退出登录
export const logout = () => {
  return request.post('/auth/logout') // 后端接口：/api/auth/logout
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request.get('/auth/currentUser') // 后端接口：/api/auth/currentUser
}