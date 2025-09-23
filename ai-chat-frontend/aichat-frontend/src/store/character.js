import { defineStore } from 'pinia'
import { getCharacterList, getCharacterDetail, createCharacter, updateCharacter, deleteCharacter, increaseCharacterPopularity } from '@/services/characterService'

export const useCharacterStore = defineStore('character', {
  state: () => ({
    characterList: [],                    // 角色列表
    selectedCharacterId: null,            // 当前选择的角色ID
    selectedCharacter: null,              // 当前选择的角色详情
    isLoading: false,                     // 是否正在加载
    searchKeyword: '',                    // 搜索关键词
    currentPage: 1,                       // 当前页码
    pageSize: 10,                         // 每页大小
    total: 0,                             // 总数
    hasMore: false                        // 是否有更多数据
  }),

  getters: {
    // 获取当前选择的角色
    currentCharacter: (state) => {
      return state.selectedCharacter
    },

    // 是否有选择的角色
    hasSelectedCharacter: (state) => {
      return state.selectedCharacterId !== null
    }
  },

  actions: {
    // 获取角色列表
    async loadCharacterList(refresh = false) {
      if (refresh) {
        this.currentPage = 1
        this.characterList = []
      }

      this.isLoading = true
      try {
        const params = {
          keyword: this.searchKeyword,
          pageNum: this.currentPage,
          pageSize: this.pageSize
        }

        const response = await getCharacterList(params)
        
        if (refresh) {
          this.characterList = response.data.records || []
        } else {
          this.characterList.push(...(response.data.records || []))
        }

        this.total = response.data.total || 0
        this.hasMore = this.characterList.length < this.total
        this.currentPage += 1

        return response.data
      } catch (error) {
        console.error('获取角色列表失败:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // 搜索角色
    async searchCharacters(keyword) {
      this.searchKeyword = keyword
      await this.loadCharacterList(true)
    },

    // 选择角色
    async selectCharacter(characterId) {
      try {
        // 如果角色已经在列表中，直接使用
        const existingCharacter = this.characterList.find(char => char.id === characterId)
        if (existingCharacter) {
          this.selectedCharacterId = characterId
          this.selectedCharacter = existingCharacter
          return existingCharacter
        }

        // 否则获取角色详情
        const response = await getCharacterDetail(characterId)
        this.selectedCharacterId = characterId
        this.selectedCharacter = response.data
        return response.data
      } catch (error) {
        console.error('选择角色失败:', error)
        throw error
      }
    },

    // 清除角色选择
    clearSelectedCharacter() {
      this.selectedCharacterId = null
      this.selectedCharacter = null
    },

    // 创建角色
    async createNewCharacter(characterData) {
      try {
        const response = await createCharacter(characterData)
        // 创建成功后刷新列表
        await this.loadCharacterList(true)
        return response.data
      } catch (error) {
        console.error('创建角色失败:', error)
        throw error
      }
    },

    // 更新角色
    async updateCharacterInfo(characterId, characterData) {
      try {
        const response = await updateCharacter(characterId, characterData)
        // 更新成功后刷新列表
        await this.loadCharacterList(true)
        return response.data
      } catch (error) {
        console.error('更新角色失败:', error)
        throw error
      }
    },

    // 删除角色
    async deleteCharacterInfo(characterId) {
      try {
        await deleteCharacter(characterId)
        // 删除成功后刷新列表
        await this.loadCharacterList(true)
        // 如果删除的是当前选择的角色，清除选择
        if (this.selectedCharacterId === characterId) {
          this.clearSelectedCharacter()
        }
      } catch (error) {
        console.error('删除角色失败:', error)
        throw error
      }
    },

    // 增加角色热度
    async increasePopularity(characterId) {
      try {
        await increaseCharacterPopularity(characterId)
      } catch (error) {
        console.error('增加角色热度失败:', error)
        // 热度增加失败不影响主要功能，只记录错误
      }
    }
  }
})
