import { createRouter, createWebHistory } from 'vue-router'
// 延迟导入组件，提高初始加载速度
const HomeView = () => import('../views/HomeView.vue')
const LoginView = () => import('../views/LoginView.vue')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { 
      requiresAuth: true,
      title: '首页'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { 
      requiresAuth: false,
      title: '登录'
    }
  },
  // 增加404页面路由
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 滚动行为：切换路由时滚动到顶部
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫：未登录拦截
router.beforeEach((to, from, next) => {
  // 防止循环重定向：如果已经在登录页，直接放行
  if (to.name === 'Login') {
    next()
    return
  }
  
  // 检查本地存储中的登录状态，避免在路由守卫中直接使用 Pinia store
  const accessToken = localStorage.getItem('accessToken')
  const refreshToken = localStorage.getItem('refreshToken')
  const tokenExpire = localStorage.getItem('tokenExpire')
  
  // 检查是否有有效的token
  const hasValidToken = accessToken && refreshToken && tokenExpire && 
                       (Date.now() < parseInt(tokenExpire))
  
  // 如果路由需要认证但用户未登录或token无效，重定向到登录页
  if (to.meta.requiresAuth && !hasValidToken) {
    // 清除无效的token
    if (accessToken || refreshToken || tokenExpire) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('tokenExpire')
      localStorage.removeItem('tokenType')
    }
    
    // 防止循环重定向：检查来源路由
    if (from.name !== 'Login') {
      next({ 
        name: 'Login',
        query: { redirect: to.fullPath } 
      })
    } else {
      // 如果是从登录页来的，直接放行（避免循环）
      next()
    }
    return
  }
  
  // 其他情况直接放行
  next()
})

export default router
    