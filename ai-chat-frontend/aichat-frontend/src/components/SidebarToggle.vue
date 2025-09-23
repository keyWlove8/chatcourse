<template>
  <div class="sidebar-toggle">
    <!-- 移动端侧边栏切换按钮 -->
    <button 
      @click="toggleSidebar"
      class="md:hidden fixed top-20 left-4 z-50 p-3 bg-blue-600 hover:bg-blue-700 text-white rounded-full shadow-lg transition-all duration-200"
      :title="isSidebarOpen ? '隐藏侧边栏' : '显示侧边栏'"
    >
      <i :class="isSidebarOpen ? 'fa fa-times' : 'fa fa-bars'"></i>
    </button>
    
    <!-- 侧边栏遮罩层 -->
    <div 
      v-if="isSidebarOpen" 
      @click="closeSidebar"
      class="md:hidden fixed inset-0 bg-black/50 z-40"
    ></div>
    
    <!-- 侧边栏容器 - 移动端浮动覆盖，桌面端正常显示 -->
    <div 
      class="sidebar-container"
      :class="{
        'sidebar-open': isSidebarOpen,
        'sidebar-closed': !isSidebarOpen
      }"
    >
      <slot />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isSidebarOpen = ref(false)

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

const closeSidebar = () => {
  isSidebarOpen.value = false
}

// 监听窗口大小变化，在桌面端自动显示侧边栏
const handleResize = () => {
  if (window.innerWidth >= 768) {
    isSidebarOpen.value = true
  } else {
    isSidebarOpen.value = false
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 暴露给父组件使用
defineExpose({
  isSidebarOpen,
  toggleSidebar,
  closeSidebar
})
</script>

<style scoped>
.sidebar-toggle {
  position: relative;
}

/* 移动端样式 - 浮动覆盖层 */
.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 280px;
  background: white;
  border-right: 1px solid #e5e7eb;
  z-index: 45;
  transition: transform 0.3s ease-in-out;
  overflow: hidden; /* 防止整个侧边栏滚动 */
  transform: translateX(-100%);
}

/* 移动端侧边栏状态 */
.sidebar-container.sidebar-open {
  transform: translateX(0);
}

.sidebar-container.sidebar-closed {
  transform: translateX(-100%);
}

/* 暗黑模式 */
.dark .sidebar-container {
  background: #111827;
  border-right-color: #374151;
}

/* 桌面端样式 - 正常布局，不影响主界面 */
@media (min-width: 768px) {
  .sidebar-container {
    position: relative;
    transform: none !important;
    width: 100%;
    height: auto;
    border-right: none;
    z-index: auto;
    overflow: hidden; /* 桌面端也防止整体滚动 */
  }
  
  /* 桌面端不需要这些状态类 */
  .sidebar-container.sidebar-open,
  .sidebar-container.sidebar-closed {
    transform: none;
  }
}
</style>
