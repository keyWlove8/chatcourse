<template>
  <div class="smart-chat-input">
    <!-- 图片预览区域 -->
    <div v-if="selectedImages.length > 0" class="mb-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-700 rounded-lg">
      <div class="flex items-center justify-between mb-3">
        <div class="flex items-center gap-2">
          <i class="fa fa-image text-yellow-600 text-sm"></i>
          <span class="text-sm font-medium text-yellow-700 dark:text-yellow-300">已选择的图片</span>
        </div>
        <button 
          @click="clearSelectedImages" 
          class="p-1 text-red-500 hover:text-red-700 hover:bg-red-100 dark:hover:bg-red-800/50 rounded transition-colors duration-200"
        >
          <i class="fa fa-times text-sm"></i>
        </button>
      </div>
      
      <!-- 图片预览网格 -->
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-2">
        <div 
          v-for="(image, index) in selectedImages" 
          :key="index"
          class="relative group"
        >
          <img 
            :src="image.preview" 
            :alt="image.name"
            class="w-full h-20 object-cover rounded-lg border-2 border-white/20 dark:border-neutral-600/30 cursor-pointer transition-all duration-300 transform hover:scale-105 hover:shadow-large"
            @click="previewImage(image)"
          />
          <button 
            @click="removeImage(index)"
            class="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity duration-200"
          >
            <i class="fa fa-times"></i>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800 shadow-sm p-4">
      <!-- 移动端布局 -->
      <div class="block md:hidden">
        <div class="space-y-3">
          <!-- 输入框 -->
          <textarea
            v-model="inputText"
            placeholder="输入您的问题..."
            class="w-full p-3 bg-gray-50 dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none"
            rows="3"
            @keydown.ctrl.enter="handleSendMessage"
          ></textarea>
          
          <!-- 移动端按钮组 -->
          <div class="flex gap-2">
            <!-- 图片上传 -->
            <button 
              @click="triggerImageUpload"
              class="flex-1 px-4 py-3 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg font-medium transition-colors duration-200"
            >
              <i class="fa fa-image mr-2"></i>添加图片
            </button>
            
            <!-- 发送按钮 -->
            <button 
              @click="handleSendMessage"
              class="flex-1 px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="isSending || !inputText.trim()"
            >
              <i v-if="isSending" class="fa fa-spinner fa-spin mr-2"></i>
              <i v-else class="fa fa-paper-plane mr-2"></i>
              发送
            </button>
          </div>
        </div>
      </div>
      
      <!-- 桌面端布局 -->
      <div class="hidden md:block">
        <div class="flex gap-3">
          <!-- 图片上传 -->
          <button 
            @click="triggerImageUpload"
            class="px-4 py-3 bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg font-medium transition-colors duration-200"
            title="添加图片 (Ctrl+I)"
          >
            <i class="fa fa-image"></i>
          </button>
          
          <!-- 输入框 -->
          <textarea
            v-model="inputText"
            placeholder="输入您的问题... (Ctrl+Enter 发送)"
            class="flex-1 p-3 bg-gray-50 dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none"
            rows="2"
            @keydown.ctrl.enter="handleSendMessage"
            @keydown.ctrl.i.prevent="triggerImageUpload"
          ></textarea>
          
          <!-- 发送按钮 -->
          <button 
            @click="handleSendMessage"
            class="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="isSending || !inputText.trim()"
            title="发送消息 (Ctrl+Enter)"
          >
            <i v-if="isSending" class="fa fa-spinner fa-spin mr-2"></i>
            <i v-else class="fa fa-paper-plane mr-2"></i>
            发送
          </button>
        </div>
      </div>
      
      <!-- 隐藏的文件输入 -->
      <input
        ref="imageInput"
        type="file"
        multiple
        accept="image/*"
        class="hidden"
        @change="handleImageSelect"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  selectedImages: {
    type: Array,
    default: () => []
  },
  inputText: {
    type: String,
    default: ''
  },
  isSending: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'update:inputText',
  'send-message',
  'image-upload',
  'clear-images',
  'remove-image',
  'preview-image'
])

const imageInput = ref(null)

const triggerImageUpload = () => {
  imageInput.value?.click()
}

const handleImageSelect = (e) => {
  emit('image-upload', e)
}

const handleSendMessage = () => {
  emit('send-message')
}

const clearSelectedImages = () => {
  emit('clear-images')
}

const removeImage = (index) => {
  emit('remove-image', index)
}

const previewImage = (image) => {
  emit('preview-image', image)
}
</script>

<style scoped>
.smart-chat-input {
  width: 100%;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .smart-chat-input textarea {
    font-size: 16px; /* 防止iOS缩放 */
  }
}

/* 桌面端优化 */
@media (min-width: 768px) {
  .smart-chat-input textarea {
    min-height: 60px;
  }
}
</style>
