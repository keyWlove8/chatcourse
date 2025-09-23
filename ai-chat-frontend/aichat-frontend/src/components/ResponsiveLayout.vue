<template>
  <div class="responsive-layout">
    <!-- 移动端布局 -->
    <div class="block md:hidden">
      <slot name="mobile" :isMobile="true" />
    </div>
    
    <!-- 桌面端布局 -->
    <div class="hidden md:block">
      <slot name="desktop" :isMobile="false" />
    </div>
    
    <!-- 通用内容（两个平台都显示） -->
    <slot />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isMobile = ref(false)
const isTablet = ref(false)
const isDesktop = ref(false)

const checkScreenSize = () => {
  const width = window.innerWidth
  isMobile.value = width < 768
  isTablet.value = width >= 768 && width < 1024
  isDesktop.value = width >= 1024
}

onMounted(() => {
  checkScreenSize()
  window.addEventListener('resize', checkScreenSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize)
})

// 暴露给父组件使用
defineExpose({
  isMobile,
  isTablet,
  isDesktop
})
</script>

<style scoped>
.responsive-layout {
  width: 100%;
}
</style>
