import request from './api'

// 发送聊天消息（携带可选的知识库ID、单张图片和memoryId）
export const sendChatMessage = (data) => {
  // 确保content存在且不为空字符串
  if (!data.content || data.content.trim() === '') {
    throw new Error('消息内容不能为空')
  }
  
  // 如果有图片，使用FormData格式
  if (data.image) {
    const formData = new FormData()
    formData.append('content', data.content)
    
    // 知识库ID是可选的 (String)
    if (data.knowledgeId) {
      formData.append('knowledgeId', data.knowledgeId)
    }
    
    // 添加memoryId（用于上下文关联）(String) - 必需参数
    formData.append('memoryId', data.memoryId)
    
    // 添加图片文件
    formData.append('image', data.image)
    
    return request.post('/chat/send', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
  
  // 没有图片时也使用FormData格式，因为后端使用@RequestParam
  const formData = new FormData()
  formData.append('content', data.content)
  formData.append('memoryId', data.memoryId)
  
  // 只有当knowledgeId存在时才添加
  if (data.knowledgeId) {
    formData.append('knowledgeId', data.knowledgeId)
  }
  
  return request.post('/chat/send', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取用户历史会话列表
export const getChatHistoryList = () => {
  return request.get('/chat/history/list')
}

// 获取指定会话的详细对话记录
export const getChatHistoryDetail = (memoryId) => {
  return request.get(`/chat/history/detail/${memoryId}`)
}

// 发送语音消息
export const sendVoiceMessage = (formData) => {
  return request.post('/chat/voice', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}