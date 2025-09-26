import axios from 'axios'
import { getAccessToken } from './token'
import config from '@/config'

/**
 * 带认证的静态资源处理工具
 * 用于处理需要认证的图片、音频等静态资源
 */

/**
 * 获取带认证的图片URL（用于img标签）
 * 通过blob URL方式实现认证
 */
export async function getAuthenticatedImageUrl(imagePath) {
  try {
    if (!imagePath) return imagePath
    
    // 如果已经是完整URL，直接返回
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath
    }
    
    // 构建完整的URL，确保请求发送到代理服务器
    const fullUrl = imagePath.startsWith('/') ? `${config.proxyUrl}${imagePath}` : imagePath
    
    // 通过axios获取图片数据
    const response = await axios.get(fullUrl, {
      responseType: 'blob',
      headers: {
        'Authorization': `Bearer ${getAccessToken()}`
      }
    })
    
    // 创建blob URL
    const blob = new Blob([response.data], { type: response.headers['content-type'] || 'image/jpeg' })
    return URL.createObjectURL(blob)
    
  } catch (error) {
    console.error('获取认证图片失败:', error)
    return imagePath // 失败时返回原路径
  }
}

/**
 * 获取带认证的音频URL（用于audio标签）
 * 通过blob URL方式实现认证
 */
export async function getAuthenticatedAudioUrl(audioPath) {
  try {
    if (!audioPath) return audioPath
    
    // 如果已经是完整URL，直接返回
    if (audioPath.startsWith('http://') || audioPath.startsWith('https://')) {
      return audioPath
    }
    
    // 构建完整的URL，确保请求发送到代理服务器
    const fullUrl = audioPath.startsWith('/') ? `${config.proxyUrl}${audioPath}` : audioPath
    
    // 通过axios获取音频数据
    const response = await axios.get(fullUrl, {
      responseType: 'blob',
      headers: {
        'Authorization': `Bearer ${getAccessToken()}`
      }
    })
    
    // 创建blob URL
    const blob = new Blob([response.data], { type: response.headers['content-type'] || 'audio/mpeg' })
    return URL.createObjectURL(blob)
    
  } catch (error) {
    console.error('获取认证音频失败:', error)
    return audioPath // 失败时返回原路径
  }
}

/**
 * 清理blob URL，释放内存
 */
export function revokeBlobUrl(url) {
  if (url && url.startsWith('blob:')) {
    URL.revokeObjectURL(url)
  }
}
