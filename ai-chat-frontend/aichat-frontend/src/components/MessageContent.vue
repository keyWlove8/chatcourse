<template>
  <div class="message-content">
    <div 
      v-for="(content, index) in contents" 
      :key="`${content.type}-${index}`" 
      class="content-item mb-3 last:mb-0 animate-fade-in"
      :style="{ animationDelay: `${index * 0.1}s` }"
    >
      <!-- 文本内容 -->
      <div v-if="content.type === 'text'" class="text-sm leading-relaxed whitespace-pre-wrap">
        {{ content.value }}
      </div>
      
      <!-- 图片内容 -->
      <div v-else-if="content.type === 'image'" class="mt-3">
        <div class="relative group">
          <img
            :src="convertImageUrl(content.value)"
            :alt="getImageAlt(content)"
            class="w-40 h-40 object-cover rounded-xl cursor-pointer transition-all duration-300 transform hover:scale-105 hover:shadow-large border-2 border-white/20 dark:border-neutral-600/30"
            @click="previewImage(content)"
            @error="handleImageError"
            loading="lazy"
          />
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-all duration-300 rounded-xl flex items-center justify-center">
            <div class="opacity-0 group-hover:opacity-100 transition-opacity duration-300 bg-white/90 dark:bg-neutral-800/90 rounded-lg px-3 py-2 shadow-medium">
              <i class="fa fa-search text-primary-600 dark:text-primary-400"></i>
            </div>
          </div>
        </div>
        <div v-if="getImageName(content)" class="text-xs text-gray-500 dark:text-gray-400 mt-2 truncate max-w-40 text-center">
          <i class="fa fa-image mr-1"></i>{{ getImageName(content) }}
        </div>
      </div>
      
      <!-- 代码内容 -->
      <div v-else-if="content.type === 'code'" class="mt-3">
        <div class="relative">
          <div class="absolute top-2 right-2 bg-neutral-800 text-neutral-200 text-xs px-2 py-1 rounded-md font-mono">
            {{ content.language || 'code' }}
          </div>
          <pre class="bg-gradient-to-r from-neutral-800 to-neutral-900 text-neutral-100 p-4 rounded-xl text-sm overflow-x-auto border border-neutral-700 shadow-medium">
            <code class="font-mono">{{ content.value }}</code>
          </pre>
        </div>
        <div class="flex justify-end mt-2">
          <button 
            @click="copyCode(content.value)"
            class="text-xs bg-neutral-200 dark:bg-neutral-700 text-neutral-700 dark:text-neutral-300 px-2 py-1 rounded-md hover:bg-neutral-300 dark:hover:bg-neutral-600 transition-colors"
            title="复制代码"
          >
            <i class="fa fa-copy mr-1"></i>复制
          </button>
        </div>
      </div>
      
      <!-- 文件内容 -->
      <div v-else-if="content.type === 'file'" class="mt-3">
        <div class="flex items-center p-4 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-xl border border-blue-200 dark:border-blue-700 shadow-soft hover:shadow-medium transition-all duration-300">
          <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-lg flex items-center justify-center mr-3">
            <i class="fa fa-file text-white"></i>
          </div>
          <div class="flex-1 min-w-0">
            <div class="text-sm font-medium text-blue-700 dark:text-blue-300 truncate">{{ content.value }}</div>
            <div class="text-xs text-blue-500 dark:text-blue-400">点击下载文件</div>
          </div>
          <button 
            @click="downloadFile(content)"
            class="ml-3 p-2 bg-gradient-to-r from-blue-500 to-indigo-500 text-white rounded-lg hover:from-blue-600 hover:to-indigo-600 transition-all duration-300 transform hover:scale-110"
            title="下载文件"
          >
            <i class="fa fa-download"></i>
          </button>
        </div>
      </div>
      
      <!-- 音频内容 -->
      <div v-else-if="content.type === 'audio'" class="mt-3">
        <div class="bg-gradient-to-r from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 rounded-xl p-4 border border-purple-200 dark:border-purple-700 shadow-soft">
          <div class="flex items-center mb-3">
            <div class="w-8 h-8 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center mr-3">
              <i class="fa fa-music text-white"></i>
            </div>
            <span class="text-sm font-medium text-purple-700 dark:text-purple-300">音频文件</span>
          </div>
          <audio controls class="w-full">
            <source :src="convertAudioUrl(content.value)" :type="content.mimeType || 'audio/mpeg'">
            您的浏览器不支持音频播放
          </audio>
        </div>
      </div>
      
      <!-- 视频内容 -->
      <div v-else-if="content.type === 'video'" class="mt-3">
        <div class="bg-gradient-to-r from-green-50 to-teal-50 dark:from-green-900/20 dark:to-teal-900/20 rounded-xl p-4 border border-green-200 dark:border-green-700 shadow-soft">
          <div class="flex items-center mb-3">
            <div class="w-8 h-8 bg-gradient-to-r from-green-500 to-teal-500 rounded-lg flex items-center justify-center mr-3">
              <i class="fa fa-video text-white"></i>
            </div>
            <span class="text-sm font-medium text-green-700 dark:text-green-300">视频文件</span>
          </div>
          <video controls class="w-full max-w-md rounded-lg">
            <source :src="content.value" :type="content.mimeType || 'video/mp4'">
            您的浏览器不支持视频播放
          </video>
        </div>
      </div>
      
      <!-- 未知类型内容 -->
      <div v-else class="mt-3 p-4 bg-gradient-to-r from-yellow-50 to-orange-50 dark:from-yellow-900/20 dark:to-orange-900/20 border border-yellow-200 dark:border-yellow-700 rounded-xl">
        <div class="flex items-center mb-2">
          <div class="w-6 h-6 bg-gradient-to-r from-yellow-500 to-orange-500 rounded-lg flex items-center justify-center mr-2">
            <i class="fa fa-exclamation-triangle text-white text-xs"></i>
          </div>
          <span class="text-sm font-medium text-yellow-800 dark:text-yellow-200">不支持的内容类型</span>
        </div>
        <div class="text-xs text-yellow-700 dark:text-yellow-300 mb-2">
          类型: {{ content.type }}
        </div>
        <div class="text-xs text-yellow-600 dark:text-yellow-400 bg-white/50 dark:bg-neutral-800/50 p-2 rounded border border-yellow-200 dark:border-yellow-600">
          {{ JSON.stringify(content, null, 2) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { convertImageUrl } from '../utils/imageUrl'
import { convertAudioUrl } from '../utils/audioUrl'

export default {
  name: 'MessageContent',
  props: {
    contents: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  methods: {
    // 将导入的函数暴露给模板使用
    convertImageUrl,
    convertAudioUrl,
    // 获取图片的alt属性
    getImageAlt(content) {
      if (content.value) {
        const url = content.value
        const fileName = url.split('/').pop().split('?')[0]
        return fileName || '图片'
      }
      return '图片'
    },
    
    // 获取图片名称
    getImageName(content) {
      if (content.value) {
        const url = content.value
        const fileName = url.split('/').pop().split('?')[0]
        return fileName || null
      }
      return null
    },
    
    // 图片预览
    previewImage(content) {
      if (content.value) {
        // 创建模态框预览
        this.createImageModal(convertImageUrl(content.value))
      }
    },
    
    // 创建图片预览模态框
    createImageModal(imageSrc) {
      const modal = document.createElement('div')
      modal.className = 'fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 animate-fade-in'
      modal.innerHTML = `
        <div class="relative max-w-4xl max-h-full p-4">
          <img src="${imageSrc}" alt="预览图片" class="max-w-full max-h-full object-contain rounded-lg shadow-2xl" />
          <button class="absolute top-4 right-4 w-10 h-10 bg-white/20 hover:bg-white/30 rounded-full flex items-center justify-center text-white transition-colors" onclick="this.parentElement.parentElement.remove()">
            <i class="fa fa-times"></i>
          </button>
        </div>
      `
      document.body.appendChild(modal)
      
      // 点击背景关闭
      modal.addEventListener('click', (e) => {
        if (e.target === modal) {
          modal.remove()
        }
      })
      
      // ESC键关闭
      document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
          modal.remove()
        }
      })
    },
    
    // 处理图片加载错误
    handleImageError(event) {
      console.error('图片加载失败:', event.target.src)
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik03NSA3NUM3NSA3NSA3NSA3NSA3NSA3NVoiIGZpbGw9IiM5Q0EzQUYiLz4KPHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIwIDIwQzIwIDIwIDIwIDIwIDIwIDIwWiIgZmlsbD0iIzlDQTNBRiIvPgo8L3N2Zz4KPC9zdmc+'
      event.target.onerror = null
    },
    
    // 复制代码
    async copyCode(code) {
      try {
        await navigator.clipboard.writeText(code)
        this.showToast('代码已复制到剪贴板', 'success')
      } catch (err) {
        // 降级方案
        const textArea = document.createElement('textarea')
        textArea.value = code
        document.body.appendChild(textArea)
        textArea.select()
        document.execCommand('copy')
        document.body.removeChild(textArea)
        this.showToast('代码已复制到剪贴板', 'success')
      }
    },
    
    // 下载文件
    downloadFile(content) {
      if (content.value) {
        const link = document.createElement('a')
        link.href = content.value
        link.download = content.value.split('/').pop().split('?')[0]
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        this.showToast('开始下载文件', 'success')
      }
    },
    
    // 显示提示信息
    showToast(message, type = 'info') {
      const toast = document.createElement('div')
      const bgColor = type === 'success' ? 'bg-success-500' : 'bg-primary-500'
      toast.className = `fixed top-4 right-4 ${bgColor} text-white px-4 py-2 rounded-lg shadow-medium z-50 animate-slide-down`
      toast.textContent = message
      document.body.appendChild(toast)
      
      setTimeout(() => {
        toast.remove()
      }, 3000)
    }
  }
}
</script>

<style scoped>
.message-content {
  /* 基础样式 */
}

.content-item {
  /* 内容项样式 */
}

/* 响应式设计 */
@media (max-width: 640px) {
  .content-item img {
    width: 8rem; /* 32 * 0.25rem */
    height: 8rem;
  }
  
  .content-item pre {
    font-size: 0.75rem;
    padding: 0.75rem;
  }
}

/* 自定义滚动条 */
.content-item pre::-webkit-scrollbar {
  height: 6px;
}

.content-item pre::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.content-item pre::-webkit-scrollbar-track {
  background: transparent;
}
</style>
