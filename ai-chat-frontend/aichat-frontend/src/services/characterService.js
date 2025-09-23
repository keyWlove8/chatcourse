import request from './api'

// 获取角色列表（分页查询）
export const getCharacterList = (params) => {
  return request.post('/character/page', params)
}

// 获取角色详情
export const getCharacterDetail = (characterId) => {
  return request.get(`/character/${characterId}`)
}

// 创建角色
export const createCharacter = (data) => {
  return request.post('/character/create', data)
}

// 更新角色
export const updateCharacter = (characterId, data) => {
  return request.put(`/character/${characterId}`, data)
}

// 删除角色
export const deleteCharacter = (characterId) => {
  return request.delete(`/character/${characterId}`)
}

// 增加角色热度
export const increaseCharacterPopularity = (characterId) => {
  return request.post(`/character/${characterId}/popularity`)
}

// 发送角色聊天消息
export const sendCharacterMessage = (data) => {
  // 确保content存在且不为空字符串
  if (!data.content || data.content.trim() === '') {
    throw new Error('消息内容不能为空')
  }
  
  // 如果有图片，使用FormData格式
  if (data.image) {
    const formData = new FormData()
    formData.append('content', data.content)
    formData.append('memoryId', data.memoryId)
    formData.append('characterId', data.characterId)
    
    // 知识库ID是可选的
    if (data.knowledgeId) {
      formData.append('knowledgeId', data.knowledgeId)
    }
    
    // 添加图片文件
    formData.append('image', data.image)
    
    return request.post('/chat/send/character', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
  
  // 没有图片时也使用FormData格式
  const formData = new FormData()
  formData.append('content', data.content)
  formData.append('memoryId', data.memoryId)
  formData.append('characterId', data.characterId)
  
  // 只有当knowledgeId存在时才添加
  if (data.knowledgeId) {
    formData.append('knowledgeId', data.knowledgeId)
  }
  
  return request.post('/chat/send/character', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 发送语音角色聊天消息
export const sendVoiceCharacterMessage = (formData) => {
  return request.post('/chat/voice/character', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传角色头像
export const uploadCharacterAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/character/upload-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
