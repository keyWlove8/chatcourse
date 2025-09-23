import CryptoJS from 'crypto-js'

/**
 * MD5加密工具类
 */
export class CryptoUtil {
  
  /**
   * MD5加密
   * @param {string} text 要加密的文本
   * @returns {string} MD5加密后的字符串
   */
  static md5(text) {
    return CryptoJS.MD5(text).toString()
  }
  
  /**
   * 密码加密（用于登录和注册）
   * @param {string} password 原始密码
   * @returns {string} MD5加密后的密码
   */
  static encryptPassword(password) {
    if (!password) {
      throw new Error('密码不能为空')
    }
    return this.md5(password)
  }
  
  /**
   * 生成随机盐值（可选，用于增强安全性）
   * @param {number} length 盐值长度
   * @returns {string} 随机盐值
   */
  static generateSalt(length = 16) {
    return CryptoJS.lib.WordArray.random(length).toString()
  }
  
  /**
   * 带盐值的密码加密（可选，用于增强安全性）
   * @param {string} password 原始密码
   * @param {string} salt 盐值
   * @returns {string} 加密后的密码
   */
  static encryptPasswordWithSalt(password, salt) {
    if (!password || !salt) {
      throw new Error('密码和盐值不能为空')
    }
    return this.md5(password + salt)
  }
}
