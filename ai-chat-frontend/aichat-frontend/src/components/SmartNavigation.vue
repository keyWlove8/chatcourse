<template>
  <header class="border-b border-gray-200 dark:border-gray-800 bg-white/80 dark:bg-gray-900/80 backdrop-blur-md">
    <div class="container mx-auto px-4 sm:px-6 py-4">
      <!-- 移动端导航 -->
      <div class="md:hidden">
        <div class="flex items-center justify-between">
          <!-- Logo区域 -->
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-yellow-400 rounded-lg flex items-center justify-center shadow-sm overflow-hidden">
              <img src="/kunkun.jpg" alt="AiChat" class="w-full h-full object-cover rounded-lg" />
            </div>
            <div>
              <h1 class="text-xl font-bold text-gray-900 dark:text-white">AiChat</h1>
              <p class="text-xs text-gray-500 dark:text-gray-400">w8助手</p>
            </div>
          </div>
          
          <!-- 移动端菜单按钮 -->
          <div class="flex items-center gap-2">
            <!-- 暗黑模式切换 -->
            <button 
              @click="toggleDarkMode" 
              class="p-2 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg transition-colors duration-200"
            >
              <i class="text-lg" :class="darkMode ? 'fa-sun text-yellow-500' : 'fa-moon text-gray-600'"></i>
            </button>
            
            <!-- 汉堡菜单 -->
            <button 
              @click="toggleMobileMenu"
              class="p-2 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg transition-colors duration-200"
            >
              <i class="fa fa-bars text-gray-600 dark:text-gray-400"></i>
            </button>
          </div>
        </div>
        
        <!-- 移动端下拉菜单 -->
        <div v-if="showMobileMenu" class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700 animate-slide-down">
          <!-- 用户信息 -->
          <div v-if="authStore.isLogin" class="mb-4 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <div class="flex items-center gap-3">
              <div class="w-2 h-2 bg-green-500 rounded-full"></div>
              <div class="flex-1">
                <div class="text-sm font-medium text-gray-900 dark:text-white">
                  {{ isUserInfoLoaded ? authStore.userInfo.username : '加载中...' }}
                </div>
                <div class="text-xs px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-md inline-block mt-1">
                  {{ authStore.userInfo.isAdmin ? '管理员' : '普通用户' }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 退出登录 -->
          <button 
            v-if="authStore.isLogin" 
            @click="authStore.logout"
            class="w-full px-4 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors duration-200"
          >
            <i class="fa fa-sign-out mr-2"></i>退出登录
          </button>
        </div>
      </div>
      
      <!-- 桌面端导航 -->
      <div class="hidden md:flex justify-between items-center">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center shadow-sm overflow-hidden">
            <img src="/kunkun.jpg" alt="AiChat" class="w-full h-full object-cover rounded-lg" />
          </div>
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">AiChat</h1>
            <p class="text-sm text-gray-500 dark:text-gray-400">w8助手</p>
          </div>
        </div>
        
        <div class="flex items-center gap-4">
          <!-- 登录状态 -->
          <div v-if="authStore.isLogin" class="flex items-center gap-3 px-4 py-2 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
            <div class="w-2 h-2 bg-green-500 rounded-full"></div>
            <div class="flex flex-col">
              <span class="text-sm font-medium text-gray-900 dark:text-white">
                {{ isUserInfoLoaded ? authStore.userInfo.username : '加载中...' }}
              </span>
              <span class="text-xs px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-md">
                {{ authStore.userInfo.isAdmin ? '管理员' : '普通用户' }}
              </span>
            </div>
          </div>
          
          <!-- 暗黑模式切换 -->
          <button 
            @click="toggleDarkMode" 
            class="p-3 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg transition-colors duration-200"
            :title="darkMode ? '切换到亮色模式' : '切换到暗色模式'"
          >
            <i class="text-lg" :class="darkMode ? 'fa-sun text-yellow-500' : 'fa-moon text-gray-600'"></i>
          </button>
          
          <!-- 退出登录 -->
          <button 
            v-if="authStore.isLogin" 
            @click="authStore.logout"
            class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors duration-200"
          >
            <i class="fa fa-sign-out mr-2"></i>退出
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const showMobileMenu = ref(false)
const darkMode = ref(false)

const isUserInfoLoaded = computed(() => {
  return authStore.userInfo && Object.keys(authStore.userInfo).length > 0
})

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const toggleDarkMode = () => {
  darkMode.value = !darkMode.value
  // 这里可以添加暗黑模式切换逻辑
}
</script>

<style scoped>
.animate-slide-down {
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
