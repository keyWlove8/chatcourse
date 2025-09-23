import { defineStore } from 'pinia'
import { login as apiLogin, logout as apiLogout, getCurrentUser } from '../services/authService'
import { setToken, removeToken } from '../utils/token'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLogin: !!localStorage.getItem('accessToken'), // 是否登录
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'), // 用户信息（isAdmin, userId, username 等）
    isLoggingIn: false // 是否正在登录中
  }),
  actions: {
    // 登录
    async login(data) {
      // 防止重复登录
      if (this.isLoggingIn) {
        return
      }
      
      this.isLoggingIn = true
      
      try {
        const res = await apiLogin(data)
        
        // 存储 Token（包含新的字段）
        setToken(
          res.data.accessToken, 
          res.data.refreshToken, 
          res.data.tokenType, 
          res.data.expiresIn
        )
        
        // 存储用户信息（包含username）
        this.userInfo = {
          isAdmin: res.data.userInfo.isAdmin,
          userId: res.data.userInfo.userId,
          username: res.data.userInfo.username
        }
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        
        // 更新登录状态
        this.isLogin = true
        
        // 跳转首页
        const redirectPath = router.currentRoute.value.query.redirect || '/'
        router.push(redirectPath)
        
      } catch (error) {
        // 优先使用后端返回的错误信息
        if (error.data && error.data.message) {
          error.message = error.data.message
        }
        throw error
      } finally {
        this.isLoggingIn = false
      }
    },
    // 退出登录
    async logout() {
      try {
        await apiLogout()
      } finally {
        // 清除 Token 和用户信息
        removeToken()
        localStorage.removeItem('userInfo')
        this.isLogin = false
        this.userInfo = {}
        // 跳转登录页
        router.push('/login')
      }
    },
    // 初始化用户信息（页面刷新时调用）
    async initUserInfo() {
      if (this.isLogin) {
        try {
          const res = await getCurrentUser()
          this.userInfo = {
            isAdmin: res.data.isAdmin,
            userId: res.data.userId,
            username: res.data.username
          }
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        } catch (err) {
          // 获取信息失败：强制登出
          this.logout()
        }
      }
    }
  }
})