<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-900 dark:via-blue-900 dark:to-indigo-900 p-4">
    <div class="w-full max-w-md animate-fade-in">
      <!-- 登录卡片 -->
      <div class="card p-8 relative overflow-hidden">
        <!-- 装饰性背景元素 -->
        <div class="absolute -top-20 -right-20 w-40 h-40 bg-gradient-to-br from-primary-200/30 to-accent-200/30 rounded-full blur-3xl"></div>
        <div class="absolute -bottom-20 -left-20 w-40 h-40 bg-gradient-to-tr from-accent-200/30 to-primary-200/30 rounded-full blur-3xl"></div>
        
        <!-- 标题区域 -->
        <div class="text-center mb-8 relative z-10">
          <div class="w-20 h-20 bg-gradient-to-br from-primary-500 to-accent-500 rounded-full flex items-center justify-center mx-auto mb-4 shadow-glow animate-float">
            <img src="/butterfly-cute.svg" alt="AiChat" class="w-12 h-12" />
          </div>
          <h2 class="text-3xl font-bold text-gradient bg-gradient-to-r from-primary-600 to-accent-600 bg-clip-text text-transparent">
            AiChat 登录
          </h2>
          <p class="text-gray-600 dark:text-gray-400 mt-2">欢迎使用知识库助手</p>
        </div>
        
        <!-- 登录表单 -->
        <form @submit.prevent="handleLogin" class="space-y-6 relative z-10">
          <div>
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
              <i class="fa fa-user mr-2 text-primary-500"></i>用户名
            </label>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              class="input-field"
              required
            >
          </div>
          
          <div>
            <label class="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-2">
              <i class="fa fa-lock mr-2 text-primary-500"></i>密码
            </label>
            <input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              class="input-field"
              required
            >
          </div>
          
          <!-- 登录按钮 -->
          <button
            type="submit"
            class="w-full btn-primary py-3 text-lg font-semibold"
            :disabled="isLoading"
          >
            <i v-if="isLoading" class="fa fa-spinner fa-spin mr-2"></i>
            <i v-else class="fa fa-sign-in mr-2"></i>
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
          
          <!-- 错误提示 -->
          <div v-if="errorMsg" class="p-4 bg-gradient-to-r from-error-50 to-red-50 dark:from-error-900/20 dark:to-red-900/20 border border-error-200 dark:border-error-700 rounded-xl animate-slide-down">
            <div class="flex items-center text-error-700 dark:text-error-300">
              <i class="fa fa-exclamation-circle mr-3 text-lg"></i>
              <span class="text-sm font-medium">{{ errorMsg }}</span>
            </div>
          </div>
        </form>
        
        <!-- 底部装饰 -->
        <div class="mt-8 text-center text-xs text-gray-500 dark:text-gray-400 relative z-10">
          <p>安全登录 · 知识管理 · 智能对话</p>
        </div>
      </div>
      
      <!-- 背景装饰元素 -->
      <div class="absolute top-1/4 left-1/4 w-2 h-2 bg-accent-400 rounded-full animate-pulse-soft"></div>
      <div class="absolute top-1/3 right-1/4 w-3 h-3 bg-primary-400 rounded-full animate-pulse-soft" style="animation-delay: 0.5s;"></div>
      <div class="absolute bottom-1/4 left-1/3 w-2 h-2 bg-accent-500 rounded-full animate-pulse-soft" style="animation-delay: 1s;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import { CryptoUtil } from '../utils/crypto'
import ButterflyIcon from '../components/ButterflyIcon.vue'

const authStore = useAuthStore()
const form = ref({ username: '', password: '' })
const isLoading = ref(false)
const errorMsg = ref('')

const handleLogin = async () => {
  isLoading.value = true
  errorMsg.value = ''
  
  try {
    // 对密码进行MD5加密
    const encryptedPassword = CryptoUtil.encryptPassword(form.value.password)
    
    // 发送加密后的密码
    await authStore.login({
      username: form.value.username,
      password: encryptedPassword
    })
  } catch (err) {
    errorMsg.value = err.message || '登录失败，请检查用户名和密码'
    
    // 添加错误动画效果
    const formElement = document.querySelector('form')
    if (formElement) {
      formElement.classList.add('animate-shake')
      setTimeout(() => {
        formElement.classList.remove('animate-shake')
      }, 500)
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 自定义动画 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.animate-shake {
  animation: shake 0.5s ease-in-out;
}

/* 输入框聚焦效果 */
.input-field:focus {
  transform: scale(1.02);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 10px 25px -5px rgba(0, 0, 0, 0.1);
}

/* 按钮悬停效果 */
.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* 响应式设计 */
@media (max-width: 640px) {
  .card {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  h2 {
    font-size: 1.75rem;
  }
  
  .w-20.h-20 {
    width: 4rem;
    height: 4rem;
  }
  
  .fa-butterfly {
    font-size: 1.5rem;
  }
}
</style>