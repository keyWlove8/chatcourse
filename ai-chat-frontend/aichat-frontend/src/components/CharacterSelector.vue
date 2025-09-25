<template>
  <div class="character-selector">
    <!-- 角色选择按钮 -->
    <div class="flex items-center gap-3">
      <button
        @click="showSelector = !showSelector"
        class="flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl font-medium text-sm"
        :class="characterStore.hasSelectedCharacter 
          ? 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white' 
          : 'bg-gradient-to-r from-red-500 to-orange-500 hover:from-red-600 hover:to-orange-600 text-white'"
      >
        <i class="fa fa-user-circle text-sm"></i>
        <span class="hidden sm:inline">
          {{ characterStore.hasSelectedCharacter ? characterStore.currentCharacter.name : '请选择角色' }}
        </span>
        <i class="fa fa-chevron-down text-xs transition-transform duration-200" :class="{ 'rotate-180': showSelector }"></i>
      </button>

      <!-- 清除选择按钮 -->
      <button
        v-if="characterStore.hasSelectedCharacter"
        @click="clearSelection"
        class="p-2 text-red-500 hover:text-red-700 hover:bg-red-100 dark:hover:bg-red-800/50 rounded-lg transition-colors duration-200"
        title="清除角色选择"
      >
        <i class="fa fa-times text-sm"></i>
      </button>
    </div>

    <!-- 角色选择面板 -->
    <Teleport to="body">
      <div
        v-if="showSelector"
        class="fixed inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center p-4 z-[9999]"
        @click="showSelector = false"
      >
        <div 
          class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 shadow-2xl overflow-hidden w-full max-w-2xl max-h-[80vh]"
          @click.stop
        >
          <!-- 搜索框 -->
          <div class="p-4 border-b border-gray-200 dark:border-gray-700">
            <div class="relative">
              <input
                v-model="searchKeyword"
                @input="handleSearch"
                type="text"
                placeholder="搜索角色..."
                class="w-full pl-10 pr-4 py-2 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-colors duration-200 text-gray-900 dark:text-gray-100"
              />
              <i class="fa fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
          </div>

          <!-- 角色列表 -->
          <div class="overflow-y-auto" style="max-height: calc(80vh - 120px);">
            <!-- 加载状态 -->
            <div v-if="characterStore.isLoading" class="p-4 text-center">
              <i class="fa fa-spinner fa-spin text-purple-500 text-lg"></i>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">加载中...</p>
            </div>

            <!-- 角色列表 -->
            <div v-else-if="characterStore.characterList.length > 0" class="p-3">
              <div
                v-for="character in characterStore.characterList"
                :key="character.id"
                @click="selectCharacter(character)"
                class="flex items-center gap-4 p-4 rounded-xl cursor-pointer transition-all duration-200 hover:bg-gray-50 dark:hover:bg-gray-700 mb-2"
                :class="{ 'bg-purple-50 dark:bg-purple-900/20 border border-purple-200 dark:border-purple-700 shadow-sm': characterStore.selectedCharacterId === character.id }"
              >
                <!-- 角色头像 -->
                <div class="w-12 h-12 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full flex items-center justify-center flex-shrink-0 shadow-md">
                  <img
                    v-if="character.avatarUrl"
                    :src="convertImageUrl(character.avatarUrl)"
                    :alt="character.name"
                    class="w-12 h-12 rounded-full object-cover"
                  />
                  <i v-else class="fa fa-user text-white text-xl"></i>
                </div>

                <!-- 角色信息 -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-3 mb-1">
                    <h3 class="font-semibold text-gray-900 dark:text-gray-100 text-lg">{{ character.name }}</h3>
                    <span v-if="character.isPublic" class="px-2 py-1 bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-300 text-xs rounded-full font-medium">
                      公开
                    </span>
                  </div>
                  <p class="text-sm text-gray-500 dark:text-gray-400 leading-relaxed">{{ character.description }}</p>
                  <!-- 音色信息 -->
                  <div v-if="character.voice" class="flex items-center gap-1 mt-2">
                    <i class="fa fa-microphone text-purple-500 text-xs"></i>
                    <span class="text-xs text-purple-600 dark:text-purple-400 font-medium">{{ character.voice.name }}</span>
                    <span class="text-xs text-gray-400">({{ getGenderLabel(character.voice.gender) }})</span>
                  </div>
                </div>

                <!-- 选择状态 -->
                <div v-if="characterStore.selectedCharacterId === character.id" class="text-purple-500">
                  <i class="fa fa-check-circle text-lg"></i>
                </div>
              </div>

              <!-- 加载更多 -->
              <div v-if="characterStore.hasMore" class="p-2">
                <button
                  @click="loadMore"
                  :disabled="characterStore.isLoading"
                  class="w-full py-2 text-sm text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <i v-if="characterStore.isLoading" class="fa fa-spinner fa-spin mr-2"></i>
                  <i v-else class="fa fa-plus mr-2"></i>
                  加载更多
                </button>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="p-8 text-center">
              <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-3">
                <i class="fa fa-user-slash text-gray-400 text-lg"></i>
              </div>
              <p class="text-sm text-gray-500 dark:text-gray-400">暂无角色</p>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useCharacterStore } from '@/store/character'
import { convertImageUrl } from '@/utils/imageUrl'

const characterStore = useCharacterStore()

const showSelector = ref(false)
const searchKeyword = ref('')

// 搜索防抖
let searchTimeout = null
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    characterStore.searchCharacters(searchKeyword.value)
  }, 300)
}

// 选择角色
const selectCharacter = async (character) => {
  try {
    await characterStore.selectCharacter(character.id)
    showSelector.value = false
  } catch (error) {
    console.error('选择角色失败:', error)
  }
}

// 清除选择
const clearSelection = () => {
  characterStore.clearSelectedCharacter()
}

// 加载更多
const loadMore = () => {
  characterStore.loadCharacterList()
}

// 监听搜索关键词变化
watch(searchKeyword, (newKeyword) => {
  if (newKeyword === '') {
    characterStore.searchCharacters('')
  }
})


// 获取性别标签
const getGenderLabel = (gender) => {
  switch (gender) {
    case 'male': return '男声'
    case 'female': return '女声'
    case 'neutral': return '中性'
    default: return '未知'
  }
}

// 组件挂载时加载角色列表
onMounted(() => {
  characterStore.loadCharacterList(true)
})
</script>

<style scoped>
.character-selector {
  position: relative;
}
</style>
