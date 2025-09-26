import { defineStore } from 'pinia'
import { getChatHistoryList, getChatHistoryDetail } from '@/services/chatService'
import { sendCharacterMessage } from '@/services/characterService'
import { convertMessageImageUrls } from '../utils/imageUrl'

// 消息类型枚举（与后端ChatMessageVO的type字段保持一致）
export const ChatMessageType = {
  USER: 'user',      // 用户消息
  AI: 'ai',          // AI回复
  SYSTEM: 'system'   // 系统消息
}

// 内容类型（与后端ContentVO的type字段保持一致，支持扩展）
export const ContentType = {
  TEXT: 'text',      // 文本内容
  IMAGE: 'image'     // 图片内容
  // 未来可以扩展更多类型，如：
  // AUDIO: 'audio',    // 音频内容
  // VIDEO: 'video',    // 视频内容
  // FILE: 'file',      // 文件内容
  // CODE: 'code',      // 代码内容
}

// 图片上传配置
const IMAGE_CONFIG = {
  MAX_SIZE: 10 * 1024 * 1024, // 10MB
  ALLOWED_TYPES: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  MAX_WIDTH: 4096,
  MAX_HEIGHT: 4096
}

export const useChatStore = defineStore('chat', {
  state: () => ({
    messages: [],                    // 当前会话的消息列表
    inputContent: '',                // 输入框内容
    isSending: false,                // 是否正在发送
    currentMemoryId: null,           // 当前会话ID (String)
    chatHistoryList: [],             // 历史会话列表
    isLoadingHistory: false,         // 是否正在加载历史记录
    selectedKnowledgeId: null,       // 当前选择的知识库ID (String)
    uploadError: null,               // 上传错误信息
    lastFailedMessage: null          // 最后失败的消息信息，用于重试
  }),
  actions: {
    // 初始化聊天，显示欢迎消息
    initChat() {
      // 如果当前没有消息，显示欢迎消息
      if (this.messages.length === 0) {
        // 生成会话ID
        this.currentMemoryId = this.generateSessionId()
        
        // 添加欢迎消息
        this.messages.push({
          id: String(Date.now()),
          memoryId: this.currentMemoryId,
          type: ChatMessageType.SYSTEM,
          contents: [{
            type: ContentType.TEXT,
            value: '你好！请问有什么可以帮助你的吗？'
          }],
          timestamp: Date.now()
        })
        
        // 添加到历史列表
        this.addToHistoryList()
      }
    },
    
    // 验证图片文件
    validateImageFile(file) {
      // 检查文件大小
      if (file.size > IMAGE_CONFIG.MAX_SIZE) {
        const errorMsg = `图片大小不能超过 ${(IMAGE_CONFIG.MAX_SIZE / 1024 / 1024).toFixed(1)}MB，当前文件大小：${(file.size / 1024 / 1024).toFixed(2)}MB`
        this.uploadError = errorMsg
        return { valid: false, error: errorMsg }
      }
      
      // 检查文件类型
      if (!IMAGE_CONFIG.ALLOWED_TYPES.includes(file.type)) {
        const errorMsg = `不支持的图片格式：${file.type}，支持的格式：${IMAGE_CONFIG.ALLOWED_TYPES.join(', ')}`
        this.uploadError = errorMsg
        return { valid: false, error: errorMsg }
      }
      
      // 检查图片尺寸
      return new Promise((resolve) => {
        const img = new Image()
        img.onload = () => {
          if (img.width > IMAGE_CONFIG.MAX_WIDTH || img.height > IMAGE_CONFIG.MAX_HEIGHT) {
            const errorMsg = `图片尺寸不能超过 ${IMAGE_CONFIG.MAX_WIDTH}x${IMAGE_CONFIG.MAX_HEIGHT}，当前尺寸：${img.width}x${img.height}`
            this.uploadError = errorMsg
            resolve({ valid: false, error: errorMsg })
          } else {
            this.uploadError = null
            resolve({ valid: true })
          }
        }
        img.onerror = () => {
          const errorMsg = '图片文件损坏或格式不支持'
          this.uploadError = errorMsg
          resolve({ valid: false, error: errorMsg })
        }
        img.src = URL.createObjectURL(file)
      })
    },
    

    // 发送角色聊天消息
    async sendCharacterMessage(characterId, knowledgeId = null, images = [], content = null, retryCount = 0) {
      // 检查是否正在发送
      if (this.isSending) {
        console.warn('正在发送消息，请稍候')
        return
      }

      // 检查是否有当前会话ID
      if (!this.currentMemoryId) {
        this.createNewChat()
      }

      // 使用传入的content或当前输入内容
      const messageContent = content || this.inputContent
      if (!messageContent || messageContent.trim() === '') {
        console.warn('消息内容不能为空')
        return
      }

      // 检查图片数量限制
      if (images.length > 1) {
        this.uploadError = '最多只能上传1张图片'
        return
      }

      // 检查图片大小
      for (const img of images) {
        if (img.file && img.file.size > IMAGE_CONFIG.MAX_SIZE) {
          this.uploadError = `图片大小不能超过 ${IMAGE_CONFIG.MAX_SIZE / 1024 / 1024}MB`
          return
        }
      }

      // 添加用户消息到列表
      const userMessage = {
        id: String(Date.now()),
        memoryId: this.currentMemoryId,
        type: ChatMessageType.USER,
        contents: [],
        timestamp: Date.now()
      }
      
      // 添加文本内容
      if (messageContent.trim()) {
        userMessage.contents.push({
          type: ContentType.TEXT,
          value: messageContent
        })
      }
      
      // 如果有图片，添加图片内容
      if (images.length > 0) {
        const image = images[0]
        userMessage.contents.push({
          type: ContentType.IMAGE,
          value: image.url || image.preview
        })
      }
      
      this.messages.push(userMessage)

      this.isSending = true

      try {
        // 准备发送的数据
        const sendData = {
          content: messageContent,
          knowledgeId: knowledgeId || null,
          memoryId: this.currentMemoryId,
          characterId: characterId,
          image: images.length > 0 ? images[0].file : undefined
        }
        
        // 调用角色聊天API
        const res = await sendCharacterMessage(sendData)
        
        // 添加AI回复到列表
        this.messages.push({
          id: String(Date.now() + 1),
          memoryId: this.currentMemoryId,
          type: ChatMessageType.AI,
          contents: [{
            type: ContentType.TEXT,
            value: res.data.reply
          }],
          timestamp: Date.now()
        })
        
        // 更新会话信息
        this.updateCurrentSessionInfo(
          messageContent,
          res.data.reply
        )
        
        // 清除错误状态
        this.uploadError = null
        this.lastFailedMessage = null
        
        // 发送成功后清空输入内容
        this.inputContent = ''
        
      } catch (err) {
        console.error('发送角色消息失败', err)
        
        // 添加错误消息到列表
        this.messages.push({
          id: String(Date.now() + 1),
          memoryId: this.currentMemoryId,
          type: ChatMessageType.SYSTEM,
          contents: [{
            type: ContentType.TEXT,
            value: this.getErrorMessage(err)
          }],
          timestamp: Date.now(),
          isError: true
        })
        
        // 保存失败的消息信息，用于重试
        this.lastFailedMessage = {
          content: messageContent,
          images: images,
          characterId: characterId,
          knowledgeId: knowledgeId
        }
        
        // 更新会话信息（即使失败也更新）
        this.updateCurrentSessionInfo(
          this.generateSessionTitle(messageContent),
          '消息发送失败'
        )
      } finally {
        this.isSending = false
      }
    },
    
    // 获取友好的错误信息
    getErrorMessage(err) {
      // 优先使用后端返回的错误信息
      if (err.data && err.data.message) {
        return err.data.message
      }
      
      if (err.message) {
        if (err.message.includes('网络')) {
          return '网络连接失败，请检查网络后重试'
        } else if (err.message.includes('timeout')) {
          return '请求超时，请稍后重试'
        } else if (err.message.includes('401')) {
          return '登录已过期，请重新登录'
        } else if (err.message.includes('403')) {
          return '权限不足，无法执行此操作'
        } else if (err.message.includes('500')) {
          return '服务器内部错误，请稍后重试'
        } else {
          return err.message
        }
      }
      return '消息发送失败，请重试'
    },
    
    // 获取历史会话列表
    async fetchChatHistoryList() {
      try {
        this.isLoadingHistory = true
        const res = await getChatHistoryList()
        
        // 处理历史会话列表，解析lastMessage中的UserMessage格式
        this.chatHistoryList = (res.data || []).map(chat => {
          if (chat.lastMessage && typeof chat.lastMessage === 'string' && 
              chat.lastMessage.includes('UserMessage') && chat.lastMessage.includes('contents = [')) {
            return {
              ...chat,
              lastMessage: this.parseUserMessageString(chat.lastMessage)
            }
          }
          return chat
        })
      } catch (err) {
        console.error('获取历史会话列表失败', err)
        this.chatHistoryList = []
      } finally {
        this.isLoadingHistory = false
      }
    },
    
    // 加载指定会话的详细对话记录
    async loadChatHistory(memoryId) {
      // 如果当前已经在同一个会话中，不需要重复加载
      if (this.currentMemoryId === memoryId) {
        return
      }
      
      try {
        this.isLoadingHistory = true
        const res = await getChatHistoryDetail(memoryId)
        
        // 设置当前会话ID
        this.currentMemoryId = memoryId
        
        // 清空当前消息列表
        this.messages = []
        
        // 加载历史消息 - 确保与 ChatDetailVO 结构一致
        if (res.data && res.data.messages) {
          
                     this.messages = res.data.messages
             .filter(msg => {
               // 过滤掉完全无效的消息
               if (!msg) return false
               
               // 检查新格式：contents 数组
               if (msg.contents && Array.isArray(msg.contents) && msg.contents.length > 0) {
                 // 只要有一个有效内容就保留消息
                 return msg.contents.some(content => {
                   if (!content) return false
                   
                   // 检查文本内容
                   if (content.text !== undefined && content.text && content.text.trim() !== '') {
                     return true
                   }
                   
                   // 检查图片内容
                   if (content.image !== undefined) {
                     if (typeof content.image === 'string' && content.image.trim() !== '') {
                       return true
                     }
                     if (content.image && typeof content.image === 'object' && content.image.url) {
                       return true
                     }
                   }
                   
                   // 检查标准格式
                   if (content.type && content.value && content.value.trim() !== '') {
                     return true
                   }
                   
                   return false
                 })
               }
               
               // 兼容旧数据格式：content 字段
               if (msg.content && typeof msg.content === 'string' && msg.content.trim() !== '') {
                 return true
               }
               
               // 如果都没有内容，但消息类型是系统消息，也保留
               if (msg.type === ChatMessageType.SYSTEM) {
                 return true
               }
               
               return false
             })
            .map(msg => {
                             // 确保每条消息都有正确的结构 - 与后端数据结构对齐
               const processedMsg = {
                 ...msg,
                 id: String(msg.id || `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`), // 生成唯一ID
                 memoryId: msg.memoryId || memoryId,
                 type: msg.type || ChatMessageType.TEXT,
                 timestamp: msg.timestamp || Date.now()
               }
              
                             // 处理 contents 字段 - 与后端数据结构对齐
               if (msg.contents && Array.isArray(msg.contents)) {
                 console.log('处理消息内容:', msg.contents) // 调试日志
                 
                                   processedMsg.contents = msg.contents.map(content => {
                    // 根据消息类型采用不同的处理策略
                    if (msg.type === 'ai' || msg.type === ChatMessageType.AI) {
                      // AI回答：直接使用文本，不需要解析JSON
                      if (typeof content === 'string') {
                        return {
                          type: ContentType.TEXT,
                          value: content
                        }
                      } else if (content.type && content.value) {
                        // 标准格式 { type: 'text', value: '...' }
                        return {
                          type: content.type,
                          value: content.value
                        }
                      } else {
                        // 其他格式，尝试提取文本
                        return {
                          type: ContentType.TEXT,
                          value: content.value || content.text || content.image || String(content)
                        }
                      }
                    } else if (msg.type === 'user' || msg.type === ChatMessageType.USER) {
                      // 用户问题：从toString格式中提取问题内容
                      if (typeof content === 'string') {
                        // 检查是否是UserMessage格式的字符串
                        if (content.includes('UserMessage') && content.includes('contents = [')) {
                          const extractedText = this.parseUserMessageString(content)
                          return {
                            type: ContentType.TEXT,
                            value: extractedText
                          }
                        } else {
                          // 普通字符串，直接使用
                          return {
                            type: ContentType.TEXT,
                            value: content
                          }
                        }
                      } else if (content.type && content.value) {
                        // 标准格式 { type: 'text', value: '...' }
                        return {
                          type: content.type,
                          value: content.value
                        }
                      } else if (content.text !== undefined) {
                        // TextContent { text = "..." }
                        return {
                          type: ContentType.TEXT,
                          value: content.text || ''
                        }
                      } else {
                        // 默认文本类型
                        return {
                          type: ContentType.TEXT,
                          value: content.value || content.text || content.image || ''
                        }
                      }
                    } else {
                      // 其他类型消息（系统消息等）：使用原有逻辑
                      if (content.type && content.value) {
                        // 标准格式 { type: 'text', value: '...' }
                        return {
                          type: content.type,
                          value: content.value
                        }
                      } else if (content.text !== undefined) {
                        // TextContent { text = "..." }
                        return {
                          type: ContentType.TEXT,
                          value: content.text || ''
                        }
                      } else if (content.image !== undefined) {
                        // ImageContent { image = Image { url = "..." } }
                        let imageUrl = ''
                        if (typeof content.image === 'string') {
                          imageUrl = content.image
                        } else if (content.image && typeof content.image === 'object') {
                          imageUrl = content.image.url || content.image.base64Data || ''
                        }
                        return {
                          type: ContentType.IMAGE,
                          value: imageUrl
                        }
                      } else {
                        // 默认文本类型
                        return {
                          type: ContentType.TEXT,
                          value: content.value || content.text || content.image || ''
                        }
                      }
                    }
                  })
                  
                  // 处理消息内容，保持原始图片路径
                  processedMsg.contents = convertMessageImageUrls(processedMsg.contents)
               } else if (msg.content) {
                 // 兼容旧数据格式
                 processedMsg.contents = [{
                   type: ContentType.TEXT,
                   value: msg.content
                 }]
               } else {
                 // 默认空内容
                 processedMsg.contents = [{
                   type: ContentType.TEXT,
                   value: ''
                 }]
               }
              
              return processedMsg
            })
          
          console.log('处理后的消息:', this.messages) // 调试日志
        }
        
        // 更新会话标题
        if (res.data && res.data.title) {
          this.updateHistoryListTitle(memoryId, res.data.title)
        }
        
        // 如果加载的历史消息为空，显示欢迎消息
        if (this.messages.length === 0) {
          // 只有在没有历史消息时才显示欢迎消息，但不要自动创建新会话
          this.messages.push({
            id: String(Date.now()),
            memoryId: memoryId,
            type: ChatMessageType.SYSTEM,
            contents: [{
              type: ContentType.TEXT,
              value: '你好！请问有什么可以帮助你的吗？'
            }],
            timestamp: Date.now()
          })
        }
        
      } catch (err) {
        console.error('加载聊天历史失败', err)
      } finally {
        this.isLoadingHistory = false
      }
    },
    
    // 创建新会话
    createNewChat() {
      // 检查当前会话是否有消息，如果没有消息则不允许新建
      if (this.messages.length === 0) {
        console.warn('当前会话没有消息，不允许新建会话')
        return false
      }
      
      // 生成唯一的会话ID
      this.currentMemoryId = this.generateSessionId()
      
      // 清空消息列表
      this.clearMessages()
      
              // 添加欢迎消息 - 完全匹配 ChatMessageVO 结构
        this.messages.push({
          id: String(Date.now()),
          memoryId: this.currentMemoryId,
          type: ChatMessageType.SYSTEM,
          contents: [{
            type: ContentType.TEXT,
            value: '你好！请问有什么可以帮助你的吗？'
          }],
          timestamp: Date.now()
        })
      
      // 添加到历史列表
      this.addToHistoryList()
      return true
    },
    
    // 生成会话ID
    generateSessionId() {
      // 使用时间戳 + 随机数生成唯一ID
      return Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    },
    
    // 解析用户问题字符串，从UserMessage格式中提取text内容
    parseUserMessageString(messageString) {
      if (!messageString || typeof messageString !== 'string') {
        return ''
      }
      
      try {
        // 查找 contents = [ 的位置
        const contentsIndex = messageString.indexOf('contents = [')
        if (contentsIndex === -1) {
          return messageString // 如果没有找到contents，直接返回原字符串
        }
        
        // 从contents开始截取
        const contentsPart = messageString.substring(contentsIndex + 'contents = ['.length)
        
        // 找到第一个 ] 的位置（数组结束）
        const arrayEndIndex = contentsPart.indexOf(']')
        if (arrayEndIndex === -1) {
          return messageString // 如果没有找到数组结束，返回原字符串
        }
        
        // 获取数组内容
        const arrayContent = contentsPart.substring(0, arrayEndIndex).trim()
        
        // 检查contents数组是否为空
        if (!arrayContent) {
          return '' // 如果contents数组为空，返回空字符串
        }
        
        // 按逗号分割，取第一个元素
        const firstElement = arrayContent.split(',')[0].trim()
        
        // 检查第一个元素是否为空
        if (!firstElement) {
          return '' // 如果第一个元素为空，返回空字符串
        }
        
        // 查找 text = "..." 的位置
        const textIndex = firstElement.indexOf('text = "')
        if (textIndex === -1) {
          return messageString // 如果没有找到text，返回原字符串
        }
        
        // 从text = "开始截取
        const textPart = firstElement.substring(textIndex + 'text = "'.length)
        
        // 找到结束的引号
        const endQuoteIndex = textPart.indexOf('"')
        if (endQuoteIndex === -1) {
          return messageString // 如果没有找到结束引号，返回原字符串
        }
        
        // 提取text内容
        return textPart.substring(0, endQuoteIndex)
      } catch (error) {
        console.warn('解析用户消息字符串失败:', error)
        return messageString // 解析失败时返回原字符串
      }
    },

    // 生成会话标题
    generateSessionTitle(content) {
      if (!content) return '新会话'
      
      // 截取前20个字符作为标题
      let title = content.trim()
      if (title.length > 20) {
        title = title.substring(0, 20) + '...'
      }
      return title
    },
    
    // 添加新会话到历史列表 - 完全匹配 ChatHistoryVO 结构
    addToHistoryList() {
      const newSession = {
        memoryId: this.currentMemoryId,           // 会话ID (String)
        lastQuestion: '',                         // 最后问题
        lastAnswer: '',                           // 最后回复
        lastTime: Date.now(),                     // 最后时间
        messageCount: 0                           // 消息数量
      }
      
      // 检查是否已存在
      const existingIndex = this.chatHistoryList.findIndex(item => item.memoryId === this.currentMemoryId)
      if (existingIndex === -1) {
        this.chatHistoryList.unshift(newSession)
      }
    },
    
    // 更新当前会话信息 - 兼容新的字段结构
    updateCurrentSessionInfo(lastQuestion, lastAnswer) {
      const index = this.chatHistoryList.findIndex(item => item.memoryId === this.currentMemoryId)
      if (index !== -1) {
        // 更新为新的字段结构
        this.chatHistoryList[index].lastQuestion = lastQuestion
        this.chatHistoryList[index].lastAnswer = lastAnswer
        
        // 同时保持向后兼容，处理lastMessage字段
        let processedLastMessage = lastAnswer
        if (typeof lastAnswer === 'string' && lastAnswer.includes('UserMessage') && lastAnswer.includes('contents = [')) {
          processedLastMessage = this.parseUserMessageString(lastAnswer)
        }
        this.chatHistoryList[index].lastMessage = processedLastMessage
        
        this.chatHistoryList[index].lastTime = Date.now()
        this.chatHistoryList[index].messageCount = this.messages.length
      }
    },
    
    // 清空消息列表
    clearMessages() {
      this.messages = []
    },
    
    // 设置输入框内容
    setInputContent(content) {
      this.inputContent = content
    },
    
    // 重置当前会话
    resetCurrentChat() {
      this.currentMemoryId = null
      this.clearMessages()
    },

    // 重试发送失败的消息
    async retryFailedMessage() {
      if (!this.lastFailedMessage) {
        console.warn('没有失败的消息需要重试')
        return
      }
      
      // 移除最后的错误消息
      if (this.messages.length > 0 && this.messages[this.messages.length - 1].isError) {
        this.messages.pop()
      }
      
      // 重新发送消息，传递失败消息的内容
      // 注意：这里需要characterId，但retryFailedMessage方法没有接收characterId参数
      // 需要从当前状态获取或传递characterId
      if (!this.lastFailedMessage.characterId) {
        console.error('重试消息时缺少角色ID')
        return
      }
      
      await this.sendCharacterMessage(
        this.lastFailedMessage.characterId,
        this.lastFailedMessage.knowledgeId,
        this.lastFailedMessage.images,
        this.lastFailedMessage.content  // 传递失败消息的内容
      )
    },

    // 更新历史列表中的会话标题
    updateHistoryListTitle(memoryId, title) {
      const index = this.chatHistoryList.findIndex(item => item.memoryId === memoryId)
      if (index !== -1) {
        this.chatHistoryList[index].title = title
      }
    }
  }
})