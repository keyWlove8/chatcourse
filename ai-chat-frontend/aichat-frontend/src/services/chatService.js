import request from './api'


// 获取用户历史会话列表
export const getChatHistoryList = () => {
  return request.get('/chat/history/list')
}

// 获取指定会话的详细对话记录
export const getChatHistoryDetail = (memoryId) => {
  return request.get(`/chat/history/detail/${memoryId}`)
}
