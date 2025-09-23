import { defineStore } from 'pinia'
import { getKnowledgeList, createKnowledge, uploadKnowledgeFile } from '../services/knowledgeService'

// 文件上传配置
const UPLOAD_CONFIG = {
  MAX_SIZE: 50 * 1024 * 1024, // 50MB
  ALLOWED_TYPES: [
    'text/plain', 'application/pdf', 'application/msword', 
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'text/csv'
  ],
  ALLOWED_EXTENSIONS: ['.txt', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.csv']
}

export const useKnowledgeStore = defineStore('knowledge', {
  state: () => ({
    knowledgeList: [], // 知识库列表
    selectedId: '',    // 当前选中的知识库ID
    isLoading: false,  // 加载状态
    uploadError: null  // 上传错误信息
  }),
  getters: {
    // 获取选中的知识库信息
    selectedKnowledge: (state) => {
      return state.knowledgeList.find(item => item.id === state.selectedId) || null
    },
    
    // 检查是否有选中的知识库
    hasSelectedKnowledge: (state) => {
      return !!state.selectedId
    }
  },
  actions: {
    // 验证上传文件
    validateUploadFile(file) {
      // 检查文件大小
      if (file.size > UPLOAD_CONFIG.MAX_SIZE) {
        const errorMsg = `文件大小不能超过 ${(UPLOAD_CONFIG.MAX_SIZE / 1024 / 1024).toFixed(0)}MB，当前文件大小：${(file.size / 1024 / 1024).toFixed(2)}MB`
        this.uploadError = errorMsg
        return { valid: false, error: errorMsg }
      }
      
      // 检查文件类型
      if (!UPLOAD_CONFIG.ALLOWED_TYPES.includes(file.type)) {
        // 如果MIME类型检查失败，检查文件扩展名
        const fileName = file.name.toLowerCase()
        const hasValidExtension = UPLOAD_CONFIG.ALLOWED_EXTENSIONS.some(ext => fileName.endsWith(ext))
        
        if (!hasValidExtension) {
          const errorMsg = `不支持的文件格式：${file.type || file.name}，支持的格式：${UPLOAD_CONFIG.ALLOWED_EXTENSIONS.join(', ')}`
          this.uploadError = errorMsg
          return { valid: false, error: errorMsg }
        }
      }
      
      this.uploadError = null
      return { valid: true }
    },
    
    // 获取知识库列表
    async fetchKnowledgeList() {
      this.isLoading = true
      try {
        const res = await getKnowledgeList()
        this.knowledgeList = res.data
        // 默认选中第一个
        if (res.data.length > 0 && !this.selectedId) {
          this.selectedId = res.data[0].id
        }
      } catch (err) {
        console.error('获取知识库列表失败', err)
      } finally {
        this.isLoading = false
      }
    },
    
    // 创建知识库
    async createKnowledge(data) {
      try {
        const res = await createKnowledge(data)
        // 返回创建的KnowledgeVO
        const newKnowledge = res.data
        
        // 重新获取列表
        await this.fetchKnowledgeList()
        
        // 自动选中新创建的知识库
        this.selectedId = newKnowledge.id
        
        return newKnowledge
      } catch (err) {
        console.error('创建知识库失败', err)
        throw err // 抛出错误让组件处理提示
      }
    },
    
    // 上传知识库文件
    async uploadKnowledgeFile(knowledgeId, file) {
      // 验证文件
      const validation = this.validateUploadFile(file)
      if (!validation.valid) {
        throw new Error(validation.error)
      }
      
      this.isLoading = true
      try {
        await uploadKnowledgeFile(knowledgeId, file)
        // 清除上传错误
        this.uploadError = null
      } catch (err) {
        console.error('上传文件失败', err)
        // 设置上传错误信息，优先使用后端返回的错误信息
        if (err.data && err.data.message) {
          this.uploadError = err.data.message
        } else if (err.message) {
          this.uploadError = err.message
        } else {
          this.uploadError = '上传失败，请重试'
        }
        throw err
      } finally {
        this.isLoading = false
      }
    },
    
    // 设置选中的知识库ID（确保只能选择一个）
    setSelectedId(id) {
      if (id && this.knowledgeList.some(item => item.id === id)) {
        this.selectedId = id
      } else {
        console.warn('无效的知识库ID:', id)
      }
    },
    
    // 清除选中的知识库
    clearSelectedId() {
      this.selectedId = ''
    },
    
    // 清除上传错误
    clearUploadError() {
      this.uploadError = null
    }
  }
})