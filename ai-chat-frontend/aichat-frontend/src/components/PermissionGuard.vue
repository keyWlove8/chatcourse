<template>
  <div v-if="hasPermission">
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../store/auth'

const props = defineProps({
  // 需要的权限，可以是字符串或数组
  permission: {
    type: [String, Array],
    required: true
  },
  // 是否反转权限检查（用于"非管理员"的情况）
  inverse: {
    type: Boolean,
    default: false
  }
})

const authStore = useAuthStore()

// 检查权限
const hasPermission = computed(() => {
  if (!authStore.isLogin) return false
  
  // 如果指定了具体权限
  if (props.permission) {
    let requiredPermissions = Array.isArray(props.permission) 
      ? props.permission 
      : [props.permission]
    
    // 检查用户是否满足任一权限要求
    const hasAnyPermission = requiredPermissions.some(permission => {
      if (permission === 'isAdmin') {
        return authStore.userInfo.isAdmin
      }
      // 可以扩展更多权限类型
      return false
    })
    
    return props.inverse ? !hasAnyPermission : hasAnyPermission
  }
  
  // 默认允许
  return true
})
</script>
