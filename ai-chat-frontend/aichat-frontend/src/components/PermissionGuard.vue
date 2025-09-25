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
  if (!authStore.isLogin) {
    console.log('PermissionGuard: 用户未登录')
    return false
  }
  
  // 如果指定了具体权限
  if (props.permission) {
    let requiredPermissions = Array.isArray(props.permission) 
      ? props.permission 
      : [props.permission]
    
    // 检查用户是否满足任一权限要求
    const hasAnyPermission = requiredPermissions.some(permission => {
      if (permission === 'isAdmin') {
        console.log('PermissionGuard: 检查管理员权限', {
          permission,
          userInfo: authStore.userInfo,
          isAdmin: authStore.userInfo.isAdmin
        })
        return authStore.userInfo.isAdmin
      }
      // 可以扩展更多权限类型
      return false
    })
    
    const result = props.inverse ? !hasAnyPermission : hasAnyPermission
    console.log('PermissionGuard: 权限检查结果', {
      permission: props.permission,
      hasAnyPermission,
      inverse: props.inverse,
      result
    })
    return result
  }
  
  // 默认允许
  return true
})
</script>
