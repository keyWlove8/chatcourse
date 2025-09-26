import config from '@/config'

/**
 * 图片URL处理工具
 * 将后端返回的图片路径转换为完整的访问URL
 */

/**
 * 转换图片URL，将路径转换为完整的访问URL
 * @param {string} imageUrl - 图片路径或URL
 * @returns {string} - 转换后的完整图片URL
 */
export function convertImageUrl(imageUrl) {
  if (!imageUrl || typeof imageUrl !== 'string') {
    return imageUrl;
  }

  // 如果已经是完整URL（包含协议），直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }

  // 如果是路径（以/开头），拼接proxyUrl
  if (imageUrl.startsWith('/')) {
    return `${config.proxyUrl}${imageUrl}`;
  }

  // 其他情况直接返回
  return imageUrl;
}

/**
 * 批量转换图片URL数组
 * @param {Array} imageUrls - 图片URL数组
 * @returns {Array} - 转换后的图片URL数组
 */
export function convertImageUrls(imageUrls) {
  if (!Array.isArray(imageUrls)) {
    return imageUrls;
  }
  
  return imageUrls.map(convertImageUrl);
}

/**
 * 转换消息内容中的图片URL
 * @param {Array} contents - 消息内容数组
 * @returns {Array} - 转换后的消息内容数组
 */
export function convertMessageImageUrls(contents) {
  if (!Array.isArray(contents)) {
    return contents;
  }
  
  return contents.map(content => {
    if (content.type === 'image' && content.value) {
      // 保持原始路径，让MessageContent组件处理认证
      return {
        ...content,
        value: content.value // 不转换，保持原始路径
      };
    }
    return content;
  });
}
