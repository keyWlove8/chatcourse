import config from '@/config'

/**
 * 音频URL处理工具
 * 将后端返回的音频路径转换为完整的访问URL
 */

/**
 * 转换音频URL，将路径转换为完整的访问URL
 * @param {string} audioUrl - 音频路径或URL
 * @returns {string} - 转换后的完整音频URL
 */
export function convertAudioUrl(audioUrl) {
  if (!audioUrl || typeof audioUrl !== 'string') {
    return audioUrl;
  }

  // 如果已经是完整URL（包含协议），直接返回
  if (audioUrl.startsWith('http://') || audioUrl.startsWith('https://')) {
    return audioUrl;
  }

  // 如果是路径（以/开头），拼接proxyUrl
  if (audioUrl.startsWith('/')) {
    return `${config.proxyUrl}${audioUrl}`;
  }

  // 其他情况直接返回
  return audioUrl;
}

/**
 * 批量转换音频URL数组
 * @param {Array} audioUrls - 音频URL数组
 * @returns {Array} - 转换后的音频URL数组
 */
export function convertAudioUrls(audioUrls) {
  if (!Array.isArray(audioUrls)) {
    return audioUrls;
  }
  
  return audioUrls.map(url => convertAudioUrl(url));
}

/**
 * 检查是否为音频文件
 * @param {string} url - 文件URL
 * @returns {boolean} - 是否为音频文件
 */
export function isAudioFile(url) {
  if (!url || typeof url !== 'string') {
    return false;
  }
  
  const audioExtensions = ['.mp3', '.wav', '.ogg', '.aac', '.m4a', '.flac', '.opus'];
  return audioExtensions.some(ext => url.toLowerCase().includes(ext));
}

/**
 * 获取音频文件类型
 * @param {string} url - 音频文件URL
 * @returns {string} - 音频文件类型
 */
export function getAudioType(url) {
  if (!url || typeof url !== 'string') {
    return 'unknown';
  }
  
  const urlLower = url.toLowerCase();
  
  if (urlLower.includes('.mp3')) return 'mp3';
  if (urlLower.includes('.wav')) return 'wav';
  if (urlLower.includes('.ogg')) return 'ogg';
  if (urlLower.includes('.aac')) return 'aac';
  if (urlLower.includes('.m4a')) return 'm4a';
  if (urlLower.includes('.flac')) return 'flac';
  if (urlLower.includes('.opus')) return 'opus';
  
  return 'unknown';
}
