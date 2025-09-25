<template>
  <div class="character-management">
    <!-- 头部操作区 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900 dark:text-white">角色管理</h2>
      <button
        @click="showCreateModal = true"
        class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
      >
        <i class="fa fa-plus mr-2"></i>创建角色
      </button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="mb-6 flex gap-4">
      <div class="flex-1">
        <input
          v-model="searchKeyword"
          @input="handleSearch"
          type="text"
          placeholder="搜索角色名称..."
          class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
        />
      </div>
      <select
        v-model="filterPublic"
        @change="handleFilter"
        class="px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
      >
        <option value="">全部角色</option>
        <option value="true">公开角色</option>
        <option value="false">私有角色</option>
      </select>
    </div>

    <!-- 角色列表 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="character in characterStore.characterList"
        :key="character.id"
        class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105"
      >
        <!-- 角色头像 -->
        <div class="p-6 text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <img
              v-if="character.avatarUrl"
              :src="convertImageUrl(character.avatarUrl)"
              :alt="character.name"
              class="w-20 h-20 rounded-full object-cover border-4 border-white dark:border-gray-700 shadow-lg"
            />
            <div
              v-else
              class="w-20 h-20 rounded-full bg-gradient-to-r from-purple-400 to-pink-400 flex items-center justify-center border-4 border-white dark:border-gray-700 shadow-lg"
            >
              <i class="fa fa-user text-white text-2xl"></i>
            </div>
            <div v-if="character.isPublic" class="absolute -top-2 -right-2 w-6 h-6 bg-green-500 rounded-full flex items-center justify-center">
              <i class="fa fa-globe text-white text-xs"></i>
            </div>
          </div>
          
          <!-- 角色信息 -->
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">{{ character.name }}</h3>
          <p class="text-sm text-gray-500 dark:text-gray-400 mb-3 line-clamp-2">{{ character.description }}</p>
          
          <!-- 角色标签 -->
          <div class="flex flex-wrap gap-1 justify-center mb-4">
            <span v-if="character.personality" class="px-2 py-1 bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 text-xs rounded-full">
              {{ character.personality }}
            </span>
            <span v-if="character.speakingStyle" class="px-2 py-1 bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-300 text-xs rounded-full">
              {{ character.speakingStyle }}
            </span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="px-6 pb-6 flex gap-2">
          <button
            @click="editCharacter(character)"
            class="flex-1 px-3 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition-colors duration-200"
          >
            <i class="fa fa-edit mr-1"></i>编辑
          </button>
          <button
            @click="deleteCharacter(character.id)"
            class="flex-1 px-3 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg text-sm font-medium transition-colors duration-200"
          >
            <i class="fa fa-trash mr-1"></i>删除
          </button>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="characterStore.hasMore" class="text-center mt-6">
      <button
        @click="loadMore"
        :disabled="characterStore.isLoading"
        class="px-6 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <i v-if="characterStore.isLoading" class="fa fa-spinner fa-spin mr-2"></i>
        <i v-else class="fa fa-plus mr-2"></i>
        加载更多
      </button>
    </div>

    <!-- 空状态 -->
    <div v-if="characterStore.characterList.length === 0 && !characterStore.isLoading" class="text-center py-12">
      <div class="w-16 h-16 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
        <i class="fa fa-user-slash text-gray-400 text-2xl"></i>
      </div>
      <p class="text-gray-500 dark:text-gray-400">暂无角色</p>
    </div>
  </div>

  <!-- 创建/编辑角色弹窗 -->
  <div v-if="showCreateModal || showEditModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
    <div class="bg-white dark:bg-gray-800 rounded-2xl p-8 w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-scale-in">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-bold text-gray-900 dark:text-white">
          {{ showEditModal ? '编辑角色' : '创建角色' }}
        </h3>
        <button
          @click="closeModal"
          class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg transition-colors duration-200"
        >
          <i class="fa fa-times text-xl"></i>
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- 基本信息 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              角色名称 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.name"
              type="text"
              required
              placeholder="请输入角色名称"
              class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              是否公开
            </label>
            <select
              v-model="form.isPublic"
              class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
            >
              <option :value="true">公开</option>
              <option :value="false">私有</option>
            </select>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            角色描述 <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="form.description"
            required
            rows="3"
            placeholder="请输入角色描述"
            class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none"
          ></textarea>
        </div>

        <!-- 角色特征 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              性格特征
            </label>
            <input
              v-model="form.personality"
              type="text"
              placeholder="如：友善、幽默、严谨"
              class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              说话风格
            </label>
            <input
              v-model="form.speakingStyle"
              type="text"
              placeholder="如：简洁明了、生动有趣"
              class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            背景故事
          </label>
          <textarea
            v-model="form.backgroundStory"
            rows="4"
            placeholder="请输入角色的背景故事"
            class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none"
          ></textarea>
        </div>

        <!-- 头像上传 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            角色头像
          </label>
          <div class="flex items-center gap-4">
            <div class="w-20 h-20 relative">
              <img
                v-if="form.avatarUrl"
                :src="convertImageUrl(form.avatarUrl)"
                :alt="form.name"
                class="w-20 h-20 rounded-full object-cover border-4 border-white dark:border-gray-700 shadow-lg"
              />
              <div
                v-else
                class="w-20 h-20 rounded-full bg-gradient-to-r from-purple-400 to-pink-400 flex items-center justify-center border-4 border-white dark:border-gray-700 shadow-lg"
              >
                <i class="fa fa-user text-white text-2xl"></i>
              </div>
            </div>
            <div class="flex-1">
              <input
                ref="avatarInput"
                type="file"
                accept="image/*"
                @change="handleAvatarSelect"
                class="hidden"
              />
              <button
                type="button"
                @click="triggerAvatarUpload"
                class="px-4 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-colors duration-200"
              >
                <i class="fa fa-upload mr-2"></i>上传头像
              </button>
              <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">支持 JPG, PNG, GIF, WebP，最大 5MB</p>
            </div>
          </div>
        </div>

        <!-- 音色选择 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            音色选择
          </label>
          <VoiceSelector 
            :selected-voice-id="form.voiceId"
            @voice-selected="handleVoiceSelected"
          />
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            为角色选择一个音色，用于语音合成
          </p>
        </div>

        <!-- 系统提示词 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            系统提示词 <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="form.systemPrompt"
            required
            rows="6"
            placeholder="请输入系统提示词，这将决定AI角色的行为模式"
            class="w-full px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none"
          ></textarea>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            系统提示词将决定AI角色的行为模式、语言风格和回答方式
          </p>
        </div>

        <!-- 操作按钮 -->
        <div class="flex justify-end gap-4 pt-6 border-t border-gray-200 dark:border-gray-700">
          <button
            type="button"
            @click="closeModal"
            class="px-6 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg transition-colors duration-200"
          >
            取消
          </button>
          <button
            type="submit"
            :disabled="isSubmitting"
            class="px-6 py-2 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          >
            <i v-if="isSubmitting" class="fa fa-spinner fa-spin mr-2"></i>
            <i v-else class="fa fa-save mr-2"></i>
            {{ showEditModal ? '更新' : '创建' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useCharacterStore } from '@/store/character'
import { uploadCharacterAvatar } from '@/services/characterService'
import { convertImageUrl } from '@/utils/imageUrl'
import VoiceSelector from './VoiceSelector.vue'

const characterStore = useCharacterStore()

// 状态管理
const showCreateModal = ref(false)
const showEditModal = ref(false)
const isSubmitting = ref(false)
const searchKeyword = ref('')
const filterPublic = ref('')
const avatarInput = ref(null)

// 表单数据
const form = reactive({
  name: '',
  description: '',
  personality: '',
  backgroundStory: '',
  speakingStyle: '',
  avatarUrl: '',
  systemPrompt: '',
  isPublic: true,
  voiceId: ''
})

// 编辑的角色ID
const editingCharacterId = ref(null)

// 搜索防抖
let searchTimeout = null
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    characterStore.searchCharacters(searchKeyword.value)
  }, 300)
}

// 筛选
const handleFilter = () => {
  characterStore.searchCharacters(searchKeyword.value)
}

// 加载更多
const loadMore = () => {
  characterStore.loadCharacterList()
}

// 打开创建弹窗
const openCreateModal = () => {
  resetForm()
  showCreateModal.value = true
}

// 编辑角色
const editCharacter = (character) => {
  editingCharacterId.value = character.id
  form.name = character.name
  form.description = character.description
  form.personality = character.personality || ''
  form.backgroundStory = character.backgroundStory || ''
  form.speakingStyle = character.speakingStyle || ''
  form.avatarUrl = character.avatarUrl || ''
  form.systemPrompt = character.systemPrompt
  form.isPublic = character.isPublic
  form.voiceId = character.voiceId || ''
  showEditModal.value = true
}

// 删除角色
const deleteCharacter = async (characterId) => {
  if (confirm('确定要删除这个角色吗？')) {
    try {
      await characterStore.deleteCharacterInfo(characterId)
    } catch (error) {
      console.error('删除角色失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.description = ''
  form.personality = ''
  form.backgroundStory = ''
  form.speakingStyle = ''
  form.avatarUrl = ''
  form.systemPrompt = ''
  form.isPublic = true
  form.voiceId = ''
  editingCharacterId.value = null
}

// 关闭弹窗
const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  resetForm()
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像选择
const handleAvatarSelect = async (event) => {
  const file = event.target.files[0]
  if (file) {
    // 检查文件大小
    if (file.size > 5 * 1024 * 1024) {
      alert('图片大小不能超过 5MB')
      return
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('只支持图片文件')
      return
    }
    
    try {
      // 先显示预览
      const reader = new FileReader()
      reader.onload = (e) => {
        form.avatarUrl = e.target.result
      }
      reader.readAsDataURL(file)
      
      // 上传到服务器
      const response = await uploadCharacterAvatar(file)
      if (response.data) {
        form.avatarUrl = response.data
      }
    } catch (error) {
      console.error('头像上传失败:', error)
      alert('头像上传失败，请重试')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!form.name.trim() || !form.description.trim() || !form.systemPrompt.trim()) {
    alert('请填写必填字段')
    return
  }

  isSubmitting.value = true

  try {
    if (showEditModal.value) {
      // 更新角色
      await characterStore.updateCharacterInfo(editingCharacterId.value, form)
    } else {
      // 创建角色
      await characterStore.createNewCharacter(form)
    }
    
    closeModal()
  } catch (error) {
    console.error('操作失败:', error)
    alert('操作失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

// 监听弹窗状态
watch([showCreateModal, showEditModal], ([create, edit]) => {
  if (create) {
    openCreateModal()
  }
})

// 处理音色选择
const handleVoiceSelected = (voice) => {
  form.voiceId = voice ? voice.id : ''
}

// 组件挂载时加载角色列表
onMounted(() => {
  characterStore.loadCharacterList(true)
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@keyframes scaleIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-scale-in {
  animation: scaleIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
</style>
