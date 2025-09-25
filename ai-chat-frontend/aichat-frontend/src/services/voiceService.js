import request from './api'

export const voiceService = {
  // 创建音色（管理员功能）
  createVoice(data) {
    return request.post('/voice/create', data)
  },

  // 获取所有音色（管理员功能）
  getAllVoices() {
    return request.get('/voice/list')
  },

  // 根据ID获取音色详情（管理员功能）
  getVoiceById(id) {
    return request.get(`/voice/${id}`)
  },

  // 更新音色（管理员功能）
  updateVoice(id, data) {
    return request.put(`/voice/${id}`, data)
  },

  // 删除音色（管理员功能）
  deleteVoice(id) {
    return request.delete(`/voice/${id}`)
  },

  // 启用/禁用音色（管理员功能）
  toggleVoiceStatus(id, enabled) {
    return request.put(`/voice/${id}/toggle`, null, {
      params: { enabled }
    })
  },

  // 查询所有启用的音色（用户功能）
  getEnabledVoices() {
    return request.get('/voice/enabled')
  },

  // 根据性别查询启用的音色（用户功能）
  getEnabledVoicesByGender(gender) {
    return request.get(`/voice/enabled/gender/${gender}`)
  },

  // 搜索音色（用户功能）
  searchVoices(keyword) {
    return request.get('/voice/search', {
      params: { keyword: keyword || '' }
    })
  }
}
