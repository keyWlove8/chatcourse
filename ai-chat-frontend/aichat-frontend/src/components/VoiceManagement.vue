<template>
  <div class="voice-management">
    <!-- 音色列表 -->
    <div class="voice-list">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-xl font-bold text-gray-900 dark:text-white">音色管理</h3>
        <button 
          @click="showCreateModal = true"
          class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
        >
          <i class="fa fa-plus mr-2"></i>添加音色
        </button>
      </div>
      
      <!-- 音色表格 -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">音色名称</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">声音码</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">性别</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">语言</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">描述</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">状态</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-600">
            <tr v-for="voice in voices" :key="voice.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ voice.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500 dark:text-gray-400">{{ voice.voiceCode }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs rounded-full" :class="getGenderClass(voice.gender)">
                  {{ voice.genderDisplay }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
                  {{ voice.languageDisplay }}
                </span>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-500 dark:text-gray-400 max-w-xs truncate">{{ voice.description }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs rounded-full" :class="voice.isEnabled ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300' : 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-300'">
                  {{ voice.isEnabled ? '启用' : '禁用' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button 
                  @click="editVoice(voice)"
                  class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 mr-3"
                >
                  编辑
                </button>
                <button 
                  @click="toggleVoiceStatus(voice)"
                  class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300 mr-3"
                >
                  {{ voice.isEnabled ? '禁用' : '启用' }}
                </button>
                <button 
                  @click="deleteVoice(voice)"
                  class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                >
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 创建/编辑音色弹窗 -->
    <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-gray-900 dark:text-white">
            {{ showEditModal ? '编辑音色' : '添加音色' }}
          </h3>
          <button 
            @click="closeModal"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
          >
            <i class="fa fa-times text-xl"></i>
          </button>
        </div>

        <form @submit.prevent="submitForm" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- 音色名称 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">音色名称 *</label>
              <input 
                v-model="form.name"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
                placeholder="请输入音色名称"
              />
            </div>

            <!-- 声音码 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">声音码 *</label>
              <input 
                v-model="form.voiceCode"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
                placeholder="请输入声音码"
              />
            </div>

            <!-- 性别 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">性别 *</label>
              <select 
                v-model="form.gender"
                required
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
              >
                <option value="">请选择性别</option>
                <option value="male">男声</option>
                <option value="female">女声</option>
                <option value="neutral">中性</option>
              </select>
            </div>

            <!-- 语言 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">语言 *</label>
              <input
                type="text"
                v-model="form.language"
                required
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
                placeholder="请输入语言代码，如：zh-CN, en-US, ja-JP等"
              />
            </div>

          </div>

          <!-- 描述 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">描述 <span class="text-red-500">*</span></label>
            <textarea 
              v-model="form.description"
              rows="3"
              required
              class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 resize-none"
              placeholder="请输入音色描述"
            ></textarea>
          </div>


          <!-- 错误提示 -->
          <div v-if="error" class="p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-700 rounded-lg">
            <div class="flex items-center text-red-700 dark:text-red-300">
              <i class="fa fa-exclamation-triangle mr-2"></i>
              <span class="text-sm">{{ error }}</span>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="flex justify-end gap-3 pt-4">
            <button 
              type="button"
              @click="closeModal"
              class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors duration-200"
            >
              取消
            </button>
            <button 
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <i v-if="isSubmitting" class="fa fa-spinner fa-spin mr-2"></i>
              {{ showEditModal ? '更新' : '创建' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { voiceService } from '@/services/voiceService'

const voices = ref([])
const showCreateModal = ref(false)
const showEditModal = ref(false)
const isSubmitting = ref(false)
const error = ref('')
const editingVoice = ref(null)

const form = ref({
  name: '',
  voiceCode: '',
  description: '',
  gender: '',
  language: ''
})

// 获取性别样式类
const getGenderClass = (gender) => {
  switch (gender) {
    case 'male': return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300'
    case 'female': return 'bg-pink-100 text-pink-800 dark:bg-pink-900/20 dark:text-pink-300'
    case 'neutral': return 'bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-300'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-300'
  }
}

// 加载音色列表
const loadVoices = async () => {
  try {
    const response = await voiceService.getAllVoices()
    voices.value = response.data
  } catch (error) {
    console.error('加载音色列表失败:', error)
  }
}

// 编辑音色
const editVoice = (voice) => {
  editingVoice.value = voice
  form.value = {
    name: voice.name,
    voiceCode: voice.voiceCode,
    description: voice.description || '',
    gender: voice.gender,
    language: voice.language
  }
  showEditModal.value = true
}

// 切换音色状态
const toggleVoiceStatus = async (voice) => {
  try {
    await voiceService.toggleVoiceStatus(voice.id, !voice.isEnabled)
    await loadVoices()
  } catch (error) {
    console.error('切换音色状态失败:', error)
  }
}

// 删除音色
const deleteVoice = async (voice) => {
  if (!confirm(`确定要删除音色"${voice.name}"吗？`)) {
    return
  }
  
  try {
    await voiceService.deleteVoice(voice.id)
    await loadVoices()
  } catch (error) {
    console.error('删除音色失败:', error)
  }
}

// 提交表单
const submitForm = async () => {
  if (isSubmitting.value) return
  
  isSubmitting.value = true
  error.value = ''
  
  try {
    if (showEditModal.value) {
      await voiceService.updateVoice(editingVoice.value.id, form.value)
    } else {
      await voiceService.createVoice(form.value)
    }
    
    await loadVoices()
    closeModal()
  } catch (error) {
    error.value = error.message || '操作失败'
  } finally {
    isSubmitting.value = false
  }
}

// 关闭弹窗
const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  editingVoice.value = null
  error.value = ''
  form.value = {
    name: '',
    voiceCode: '',
    description: '',
    gender: '',
    language: ''
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadVoices()
})
</script>

<style scoped>
.voice-management {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
