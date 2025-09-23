import request from './api'

// 获取知识库列表
export const getKnowledgeList = () => {
  return request.get('/knowledge/list') // 后端接口：/api/knowledge/list
}

// 创建知识库
export const createKnowledge = (data) => {
  return request.post('/knowledge/create', data) // 后端接口：/api/knowledge/create
}

// 上传知识库文件
export const uploadKnowledgeFile = (knowledgeId, file) => {
  const formData = new FormData()
  formData.append('knowledgeId', knowledgeId)
  formData.append('file', file)
  return request.post('/knowledge/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }) // 后端接口：/api/knowledge/upload
}