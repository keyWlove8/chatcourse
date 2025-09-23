<template>
  <div class="character-selector">
    <!-- 角色选择按钮 -->
    <div class="flex items-center gap-3">
      <button
        @click="showSelector = !showSelector"
        class="flex items-center gap-2 px-4 py-2 rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl"
        :class="characterStore.hasSelectedCharacter 
          ? 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white ring-2 ring-purple-300' 
          : 'bg-gradient-to-r from-red-500 to-orange-500 hover:from-red-600 hover:to-orange-600 text-white ring-2 ring-red-300'"
      >
        <i class="fa fa-user-circle text-lg"></i>
        <span class="font-medium">
          {{ characterStore.hasSelectedCharacter ? characterStore.currentCharacter.name : '请选择角色' }}
        </span>
        <i class="fa fa-chevron-down text-sm transition-transform duration-200" :class="{ 'rotate-180': showSelector }"></i>
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
    <div
      v-if="showSelector"
      class="absolute top-full left-0 right-0 mt-2 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 shadow-xl z-50 max-h-96 overflow-hidden"
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
      <div class="max-h-64 overflow-y-auto">
        <!-- 加载状态 -->
        <div v-if="characterStore.isLoading" class="p-4 text-center">
          <i class="fa fa-spinner fa-spin text-purple-500 text-lg"></i>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">加载中...</p>
        </div>

        <!-- 角色列表 -->
        <div v-else-if="characterStore.characterList.length > 0" class="p-2">
          <div
            v-for="character in characterStore.characterList"
            :key="character.id"
            @click="selectCharacter(character)"
            class="flex items-center gap-3 p-3 rounded-lg cursor-pointer transition-all duration-200 hover:bg-gray-50 dark:hover:bg-gray-700"
            :class="{ 'bg-purple-50 dark:bg-purple-900/20 border border-purple-200 dark:border-purple-700': characterStore.selectedCharacterId === character.id }"
          >
            <!-- 角色头像 -->
            <div class="w-10 h-10 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full flex items-center justify-center flex-shrink-0">
              <img
                v-if="character.avatarUrl"
                :src="convertImageUrl(character.avatarUrl)"
                :alt="character.name"
                class="w-10 h-10 rounded-full object-cover"
              />
              <i v-else class="fa fa-user text-white text-lg"></i>
            </div>

            <!-- 角色信息 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <h3 class="font-medium text-gray-900 dark:text-gray-100 truncate">{{ character.name }}</h3>
                <span v-if="character.isPublic" class="px-2 py-1 bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-300 text-xs rounded-full">
                  公开
                </span>
              </div>
              <p class="text-sm text-gray-500 dark:text-gray-400 truncate">{{ character.description }}</p>
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
