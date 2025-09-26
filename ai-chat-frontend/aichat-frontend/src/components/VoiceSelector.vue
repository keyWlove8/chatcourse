<template>
  <div class="voice-selector" @click.stop>
    <!-- 音色选择按钮 -->
    <div class="flex items-center gap-3">
      <button
        type="button"
        @click.stop="showModal = true"
        class="flex items-center gap-3 px-4 py-2 rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl"
        :class="selectedVoice 
          ? 'bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white ring-2 ring-green-300' 
          : 'bg-gradient-to-r from-gray-500 to-gray-600 hover:from-gray-600 hover:to-gray-700 text-white ring-2 ring-gray-300'"
      >
        <i class="fa fa-microphone text-lg"></i>
        <span class="font-semibold text-sm">
          {{ selectedVoice ? selectedVoice.name : '选择音色' }}
        </span>
        <i class="fa fa-chevron-right text-xs"></i>
      </button>

      <!-- 清除选择按钮 -->
      <button
        type="button"
        v-if="selectedVoice"
        @click.stop="clearSelection"
        class="p-2 text-red-500 hover:text-red-700 hover:bg-red-100 dark:hover:bg-red-800/50 rounded-lg transition-colors duration-200"
        title="清除音色选择"
      >
        <i class="fa fa-times text-sm"></i>
      </button>
    </div>

    <!-- 音色选择弹框 -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center p-4 z-[9999]"
        @click="showModal = false"
      >
        <div 
          class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 shadow-2xl overflow-hidden w-full max-w-2xl max-h-[80vh]"
          @click.stop
        >
          <!-- 弹框头部 -->
          <div class="p-4 border-b border-gray-200 dark:border-gray-700">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-gray-900 dark:text-white">选择音色</h2>
              <button
                type="button"
                @click="showModal = false"
                class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg transition-colors duration-200"
              >
                <i class="fa fa-times text-xl"></i>
              </button>
            </div>
          </div>

          <!-- 搜索和筛选区域 -->
          <div class="p-4 border-b border-gray-200 dark:border-gray-700">
            <!-- 搜索框 -->
            <div class="relative mb-3">
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索音色..."
                class="w-full pl-10 pr-4 py-2 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
              />
              <i class="fa fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
            
            <!-- 性别筛选 -->
            <div class="flex gap-2">
              <button
                type="button"
                @click="selectedGender = ''"
                class="px-3 py-1 text-xs rounded-full transition-colors duration-200"
                :class="selectedGender === '' 
                  ? 'bg-green-500 text-white' 
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
              >
                全部
              </button>
              <button
                type="button"
                @click="selectedGender = 'male'"
                class="px-3 py-1 text-xs rounded-full transition-colors duration-200"
                :class="selectedGender === 'male' 
                  ? 'bg-blue-500 text-white' 
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
              >
                男声
              </button>
              <button
                type="button"
                @click="selectedGender = 'female'"
                class="px-3 py-1 text-xs rounded-full transition-colors duration-200"
                :class="selectedGender === 'female' 
                  ? 'bg-pink-500 text-white' 
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
              >
                女声
              </button>
              <button
                type="button"
                @click="selectedGender = 'neutral'"
                class="px-3 py-1 text-xs rounded-full transition-colors duration-200"
                :class="selectedGender === 'neutral' 
                  ? 'bg-gray-500 text-white' 
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
              >
                中性
              </button>
            </div>
            
            <!-- 语言筛选 -->
            <div class="mt-2">
              <label class="block text-xs font-medium text-gray-600 dark:text-gray-400 mb-1">语言筛选</label>
              <input
                type="text"
                v-model="selectedLanguage"
                class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
                placeholder="输入语言代码筛选，如：zh-CN"
              />
            </div>
          </div>

          <!-- 音色列表 -->
          <div class="overflow-y-auto" style="max-height: calc(80vh - 200px);">
            <!-- 加载状态 -->
            <div v-if="isLoading" class="p-4 text-center">
              <i class="fa fa-spinner fa-spin text-green-500 text-lg"></i>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">加载中...</p>
            </div>

            <!-- 音色列表 -->
            <div v-else-if="filteredVoices.length > 0" class="p-3">
              <div
                v-for="voice in filteredVoices"
                :key="voice.id"
                @click="selectVoice(voice)"
                class="flex items-center gap-4 p-3 rounded-xl cursor-pointer transition-all duration-200 hover:bg-gray-50 dark:hover:bg-gray-700 mb-2"
                :class="{ 'bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-700 shadow-sm': selectedVoice?.id === voice.id }"
              >
                <!-- 音色图标 -->
                <div class="w-12 h-12 bg-gradient-to-r from-green-400 to-emerald-400 rounded-full flex items-center justify-center flex-shrink-0 shadow-md">
                  <i class="fa fa-microphone text-white text-lg"></i>
                </div>

                <!-- 音色信息 -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-3 mb-1">
                    <h3 class="font-semibold text-gray-900 dark:text-gray-100 text-lg">{{ voice.name }}</h3>
                    <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 text-xs rounded-full font-medium">
                      {{ voice.genderDisplay }}
                    </span>
                    <span class="px-2 py-1 bg-blue-100 dark:bg-blue-700 text-blue-700 dark:text-blue-300 text-xs rounded-full font-medium">
                      {{ voice.languageDisplay }}
                    </span>
                  </div>
                  <p class="text-sm text-gray-500 dark:text-gray-400 leading-relaxed">{{ voice.description }}</p>
                  <div class="text-xs text-gray-400 dark:text-gray-500 mt-1">{{ voice.voiceCode }}</div>
                </div>

                <!-- 选择状态 -->
                <div v-if="selectedVoice?.id === voice.id" class="text-green-500">
                  <i class="fa fa-check-circle text-lg"></i>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="p-8 text-center">
              <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-3">
                <i class="fa fa-microphone-slash text-gray-400 text-lg"></i>
              </div>
              <p class="text-sm text-gray-500 dark:text-gray-400">暂无音色</p>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { voiceService } from '@/services/voiceService'

const props = defineProps({
  selectedVoiceId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['voice-selected'])

const showModal = ref(false)
const searchKeyword = ref('')
const selectedGender = ref('')
const selectedLanguage = ref('')
const isLoading = ref(false)
const voices = ref([])
const selectedVoice = ref(null)

// 筛选后的音色列表
const filteredVoices = computed(() => {
  let filtered = voices.value
  
  // 按性别筛选
  if (selectedGender.value) {
    filtered = filtered.filter(voice => voice.gender === selectedGender.value)
  }
  
  // 按语言筛选
  if (selectedLanguage.value) {
    filtered = filtered.filter(voice => voice.language.toLowerCase().includes(selectedLanguage.value.toLowerCase()))
  }
  
  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(voice => 
      voice.name.toLowerCase().includes(keyword) ||
      voice.description.toLowerCase().includes(keyword) ||
      voice.voiceCode.toLowerCase().includes(keyword)
    )
  }
  
  return filtered
})

// 选择音色
const selectVoice = (voice) => {
  selectedVoice.value = voice
  emit('voice-selected', voice)
  showModal.value = false
}

// 清除选择
const clearSelection = () => {
  selectedVoice.value = null
  emit('voice-selected', null)
}

// 加载音色列表
const loadVoices = async () => {
  isLoading.value = true
  try {
    const response = await voiceService.getEnabledVoices()
    if (response && response.data) {
      voices.value = response.data
    } else {
      voices.value = []
    }
  } catch (error) {
    console.error('加载音色列表失败:', error)
    voices.value = []
  } finally {
    isLoading.value = false
  }
}

// 监听props变化，设置选中的音色
watch(() => props.selectedVoiceId, (newVoiceId) => {
  if (newVoiceId && voices.value.length > 0) {
    const voice = voices.value.find(v => v.id === newVoiceId)
    if (voice) {
      selectedVoice.value = voice
    }
  } else if (!newVoiceId) {
    selectedVoice.value = null
  }
}, { immediate: true })

// 组件挂载时加载音色列表
onMounted(() => {
  loadVoices()
})
</script>

<style scoped>
/* No specific styles needed for .voice-selector as it's now a modal */
</style>