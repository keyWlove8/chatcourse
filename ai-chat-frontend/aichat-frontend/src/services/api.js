import axios from 'axios'
import { getAccessToken, getRefreshToken, setToken, isTokenExpired, removeToken } from '../utils/token'
import router from '../router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 修复：所有API请求都使用/api前缀
  timeout: 120000, // 增加到2分钟，适应大模型响应时间
  headers: { 'Content-Type': 'application/json' }
})

// 是否正在刷新Token
let isRefreshing = false
// 等待刷新Token的请求队列
let failedQueue = []

// 处理等待中的请求
const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  
  failedQueue = []
}

// 请求拦截器：添加 Token
request.interceptors.request.use(
  async (config) => {
    // Token 未过期：添加到请求头
    if (!isTokenExpired() && getAccessToken()) {
      config.headers.Authorization = `Bearer ${getAccessToken()}`
    }
    // Token 已过期：尝试刷新
    else if (getRefreshToken()) {
      try {
        // 如果正在刷新，将请求加入队列
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject })
          }).then(token => {
            config.headers.Authorization = `Bearer ${token}`
            return config
          }).catch(err => {
            return Promise.reject(err)
          })
        }
        
        isRefreshing = true
        
        const res = await axios.post('/auth/refresh', {
          refreshToken: getRefreshToken()
        })
        
        // 刷新成功：更新 Token 并重新添加到请求头
        setToken(res.data.accessToken, res.data.refreshToken)
        config.headers.Authorization = `Bearer ${res.data.accessToken}`
        
        // 处理等待中的请求
        processQueue(null, res.data.accessToken)
        
      } catch (err) {
        // 刷新失败：清除 Token 并跳转登录
        processQueue(err, null)
        removeToken()
        router.push('/login?redirect=' + router.currentRoute.value.fullPath)
        return Promise.reject(new Error('Token 已过期，请重新登录'))
      } finally {
        isRefreshing = false
      }
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误和Result结构
request.interceptors.response.use(
  (res) => {
    // 检查后端返回的Result结构
    const result = res.data
    
    // 如果后端返回的是Result结构
    if (result && typeof result === 'object' && 'success' in result) {
      if (result.success) {
        // 成功：直接返回data部分
        return result
      } else {
        // 失败：抛出业务异常
        const error = new Error(result.message || '操作失败')
        error.code = result.code
        error.data = result
        return Promise.reject(error)
      }
    }
    
    // 如果不是Result结构，直接返回
    return res.data
  },
  (error) => {
    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response
      
      // 401：未登录或 Token 无效
      if (status === 401) {
        removeToken()
        router.push('/login?redirect=' + router.currentRoute.value.fullPath)
        return Promise.reject(new Error('登录已过期，请重新登录'))
      }
      
      // 403：权限不足
      if (status === 403) {
        return Promise.reject(new Error('权限不足，无法执行此操作'))
      }
      
      // 404：资源不存在
      if (status === 404) {
        return Promise.reject(new Error('请求的资源不存在'))
      }
      
      // 500：服务器错误
      if (status >= 500) {
        return Promise.reject(new Error('服务器内部错误，请稍后重试'))
      }
      
      // 其他HTTP错误
      if (data && data.message) {
        return Promise.reject(new Error(data.message))
      }
    }
    
    // 网络错误
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      return Promise.reject(new Error('请求超时，请检查网络连接'))
    }
    
    if (error.message.includes('Network Error')) {
      return Promise.reject(new Error('网络连接失败，请检查网络'))
    }
    
    // 其他错误
    return Promise.reject(error.response?.data || { message: '网络请求失败' })
  }
)

export default request