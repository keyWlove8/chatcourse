<template>
  <div v-if="show" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-[60]">
    <div class="bg-white dark:bg-gray-800 rounded-2xl p-8 w-full max-w-md relative">
      <!-- 关闭按钮 -->
      <button 
        @click="closeModal"
        class="absolute top-4 right-4 w-8 h-8 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-full flex items-center justify-center transition-colors duration-200 z-10"
        title="关闭语音通话"
      >
        <i class="fa fa-times text-gray-600 dark:text-gray-300 text-sm"></i>
      </button>
      <!-- 角色头像区域 -->
      <div class="text-center mb-6">
        <div class="relative inline-block">
          <!-- 角色头像 -->
          <div class="w-32 h-32 mx-auto mb-4 relative">
            <img
              v-if="characterAvatar"
              :src="authenticatedAvatarUrl"
              :alt="characterName"
              class="w-32 h-32 rounded-full object-cover border-4 border-white dark:border-gray-700 shadow-2xl"
            />
            <div
              v-else
              class="w-32 h-32 rounded-full bg-gradient-to-r from-purple-400 to-pink-400 flex items-center justify-center border-4 border-white dark:border-gray-700 shadow-2xl"
            >
              <i class="fa fa-user text-white text-4xl"></i>
            </div>
            
            <!-- 通话状态指示器 -->
            <div class="absolute -bottom-2 -right-2 w-8 h-8 bg-green-500 rounded-full flex items-center justify-center animate-pulse">
              <i class="fa fa-phone text-white text-sm"></i>
            </div>
          </div>
          
          <!-- 角色名称 -->
          <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">{{ characterName }}</h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">正在通话中...</p>
        </div>
      </div>

      <!-- 通话控制区域 -->
      <div class="space-y-4">
        <!-- 音量波浪显示 -->
        <div v-if="callState !== 'idle'" class="flex justify-center items-center space-x-1 mb-4">
          <div 
            v-for="(bar, index) in volumeBars" 
            :key="index"
            :style="{ height: bar + 'px' }"
            class="bg-gradient-to-t from-blue-500 to-purple-500 rounded-full transition-all duration-100 ease-out"
            :class="bar > 0 ? 'opacity-100' : 'opacity-30'"
          ></div>
        </div>
        
        <!-- 通话状态 -->
        <div class="text-center">
          <div v-if="callState === 'processing'" class="flex items-center justify-center gap-2 text-blue-500">
            <i class="fa fa-spinner fa-spin"></i>
            <span class="text-sm font-medium">正在思考...</span>
          </div>
          <div v-else-if="callState === 'playing'" class="flex items-center justify-center gap-2 text-yellow-500">
            <i class="fa fa-volume-up"></i>
            <span class="text-sm font-medium">AI正在回复...</span>
          </div>
          <div v-else-if="callState === 'recording'" class="flex items-center justify-center gap-2 text-green-500">
            <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
            <span class="text-sm font-medium">请说话...</span>
          </div>
          <div v-else-if="callState === 'listening'" class="flex items-center justify-center gap-2 text-green-500">
            <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
            <span class="text-sm font-medium">通话中，请说话...</span>
          </div>
          <div v-else class="flex items-center justify-center gap-2 text-gray-500">
            <i class="fa fa-phone"></i>
            <span class="text-sm font-medium">准备通话...</span>
          </div>
        </div>


        <!-- 等待提示消息 -->
        <div v-if="showWaitingMessage" class="flex justify-center items-center gap-2 text-amber-500 bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-700 rounded-lg p-3 mx-4">
          <i class="fa fa-clock text-amber-600"></i>
          <span class="text-sm font-medium">请稍等，正在回复中...</span>
        </div>

        <!-- 控制按钮 -->
        <div class="flex justify-center gap-4">
          <!-- 字幕按钮 -->
          <button
            v-if="callState !== 'idle'"
            @click="showSubtitleModal = true"
            class="px-4 py-2 rounded-full bg-blue-500 hover:bg-blue-600 text-white flex items-center justify-center gap-2 transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
            title="查看字幕"
          >
            <i class="fa fa-closed-captioning"></i>
            <span class="text-sm font-medium">字幕</span>
          </button>

          <!-- 挂断按钮 -->
          <button
            v-if="callState !== 'idle'"
            @click="endCall"
            class="w-16 h-16 rounded-full bg-red-500 hover:bg-red-600 text-white flex items-center justify-center transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
            title="挂断电话"
          >
            <i class="fa fa-phone-slash"></i>
          </button>
        </div>

        <!-- AI语音回复（隐藏播放器，直接播放） -->
        <div v-if="currentAudioUrl" class="hidden">
          <audio :src="currentAudioUrl" autoplay>
            您的浏览器不支持音频播放
          </audio>
        </div>

      </div>
    </div>

      <!-- 字幕弹框（显示在右侧） -->
      <div v-if="showSubtitleModal" class="fixed top-4 right-4 w-80 max-h-[80vh] z-[70]">
        <div class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 shadow-2xl overflow-hidden">
        <!-- 弹框头部 -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-blue-500 to-purple-500">
          <div class="flex justify-between items-center">
            <h2 class="text-xl font-bold text-white flex items-center gap-2">
              <i class="fa fa-closed-captioning"></i>
              通话字幕
            </h2>
            <button
              @click="showSubtitleModal = false"
              class="p-2 text-white/80 hover:text-white hover:bg-white/20 rounded-lg transition-colors duration-200"
            >
              <i class="fa fa-times text-xl"></i>
            </button>
          </div>
        </div>

        <!-- 字幕内容 -->
        <div class="overflow-y-auto" style="max-height: calc(70vh - 120px);">
          <div v-if="conversationHistory.length === 0" class="p-8 text-center">
            <div class="w-16 h-16 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
              <i class="fa fa-comments text-gray-400 text-2xl"></i>
            </div>
            <p class="text-sm text-gray-500 dark:text-gray-400">暂无对话记录</p>
          </div>

          <div v-else class="p-4 space-y-4">
            <div
              v-for="(item, index) in conversationHistory"
              :key="index"
              class="space-y-3"
            >
              <!-- 用户消息 -->
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center flex-shrink-0">
                  <i class="fa fa-user text-white text-sm"></i>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3">
                    <div class="text-xs text-blue-600 dark:text-blue-400 mb-1 font-medium">您说：</div>
                    <p class="text-sm text-gray-900 dark:text-gray-100 leading-relaxed">{{ item.userText }}</p>
                    <div class="text-xs text-gray-400 dark:text-gray-500 mt-1">{{ formatTime(item.timestamp) }}</div>
                  </div>
                </div>
              </div>

              <!-- AI回复 -->
              <div class="flex items-start gap-3">
                <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 overflow-hidden">
                  <img
                    v-if="characterAvatar"
                    :src="getCachedAvatarUrl(characterAvatar)"
                    :alt="characterName"
                    class="w-8 h-8 rounded-full object-cover"
                  />
                  <div
                    v-else
                    class="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center"
                  >
                    <i class="fa fa-robot text-white text-sm"></i>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-3">
                    <div class="text-xs text-purple-600 dark:text-purple-400 mb-1 font-medium">{{ characterName }}回复：</div>
                    <p class="text-sm text-gray-900 dark:text-gray-100 leading-relaxed">{{ item.aiText }}</p>
                    <div class="text-xs text-gray-400 dark:text-gray-500 mt-1">{{ formatTime(item.timestamp) }}</div>
                    
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { convertAudioUrl } from '@/utils/audioUrl'
import { convertImageUrl } from '@/utils/imageUrl'
import { getAuthenticatedImageUrl, getAuthenticatedAudioUrl } from '@/utils/authenticatedResource'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  characterName: {
    type: String,
    default: 'AI助手'
  },
  characterAvatar: {
    type: String,
    default: ''
  },
  characterId: {
    type: String,
    default: ''
  },
  memoryId: {
    type: String,
    default: ''
  },
  knowledgeId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'send-voice', 'add-message'])

// 字幕相关状态
const showSubtitleModal = ref(false)
const conversationHistory = ref([])  // 存储对话历史
const currentAudioUrl = ref('')
const authenticatedAvatarUrl = ref('')
const showWaitingMessage = ref(false)

// 头像缓存
const avatarCache = ref(new Map())

// 通话模式相关状态 - 简化状态管理
const callState = ref('idle') // 'idle' | 'listening' | 'recording' | 'processing' | 'playing'
const volumeBars = ref(new Array(12).fill(0))  // 音量波浪条

// 语音检测相关
let audioContext = null
let analyser = null
let dataArray = null
let animationId = null
let silenceTimer = null
let mediaRecorder = null
let audioChunks = []
let isSilenceCounting = false  // 是否正在静音计时
let noiseLevel = 0.05  // 环境噪音水平
let samplesCount = 0   // 采样计数
let currentAudio = null  // 当前播放的音频对象

// 语音检测参数
const VOICE_THRESHOLD = 0.15     // 语音阈值 - 开始录音的阈值（提高以过滤环境噪音）
const VOICE_RESUME_THRESHOLD = 0.20  // 语音恢复阈值 - 重新开始计时的阈值（更高）
const SILENCE_DURATION = 2000    // 静音持续时间(ms) - 2秒

// 清理所有资源
const cleanup = () => {
  console.log('开始清理资源')
  
  // 重置状态
  callState.value = 'idle'
  showSubtitleModal.value = false
  volumeBars.value = new Array(12).fill(0)
  currentAudioUrl.value = ''  // 清理音频URL
  
  // 停止当前播放的音频
  if (currentAudio) {
    try {
      currentAudio.pause()
      currentAudio.currentTime = 0
      currentAudio = null
    } catch (error) {
      console.warn('停止音频播放时出错:', error)
    }
  }
  
  // 清理定时器
  if (silenceTimer) {
    clearTimeout(silenceTimer)
    silenceTimer = null
  }
  isSilenceCounting = false
  noiseLevel = 0.05
  samplesCount = 0
  
  // 清理动画
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
  
  // 停止录音
  if (mediaRecorder && mediaRecorder.state === 'recording') {
    try {
      mediaRecorder.stop()
    } catch (error) {
      console.warn('停止录音时出错:', error)
    }
  }
  
  // 停止音频轨道
  if (mediaRecorder && mediaRecorder.stream) {
    try {
      mediaRecorder.stream.getTracks().forEach(track => {
        track.stop()
      })
    } catch (error) {
      console.warn('停止音频轨道时出错:', error)
    }
  }
  
  // 关闭音频上下文
  if (audioContext && audioContext.state !== 'closed') {
    try {
      audioContext.close()
    } catch (error) {
      console.warn('关闭音频上下文时出错:', error)
    }
  }
  
  // 清理引用
  mediaRecorder = null
  audioContext = null
  analyser = null
  dataArray = null
  audioChunks = []
  
  console.log('资源清理完成')
}

// 开始通话
const startCall = async () => {
  try {
    console.log('开始通话')
    cleanup()
    
    // 获取麦克风权限
    const stream = await navigator.mediaDevices.getUserMedia({ 
      audio: {
        sampleRate: 16000,
        channelCount: 1,
        echoCancellation: true,
        noiseSuppression: true
      }
    })
    
    // 创建音频上下文
    audioContext = new (window.AudioContext || window.webkitAudioContext)()
    console.log('音频上下文采样率:', audioContext.sampleRate)
    
    // 创建音频分析器
    const source = audioContext.createMediaStreamSource(stream)
    analyser = audioContext.createAnalyser()
    analyser.fftSize = 256
    analyser.smoothingTimeConstant = 0.8
    source.connect(analyser)
    
    dataArray = new Uint8Array(analyser.frequencyBinCount)
    
    // 设置录音器 - 优先使用WebM格式，然后转换为WAV
    let mimeType = 'audio/webm;codecs=opus'
    if (MediaRecorder.isTypeSupported('audio/webm;codecs=opus')) {
      mimeType = 'audio/webm;codecs=opus'
    } else if (MediaRecorder.isTypeSupported('audio/ogg;codecs=opus')) {
      mimeType = 'audio/ogg;codecs=opus'
    } else if (MediaRecorder.isTypeSupported('audio/wav')) {
      mimeType = 'audio/wav'
    } else if (MediaRecorder.isTypeSupported('audio/mp3')) {
      mimeType = 'audio/mp3'
    } else if (MediaRecorder.isTypeSupported('audio/aac')) {
      mimeType = 'audio/aac'
    }
    
    console.log('使用音频格式:', mimeType)
    mediaRecorder = new MediaRecorder(stream, { mimeType })
    audioChunks = []
    
    mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        audioChunks.push(event.data)
      }
    }
    
    mediaRecorder.onstop = async () => {
      console.log('录音停止，处理音频数据')
      const audioBlob = new Blob(audioChunks, { type: mimeType })
      
      // 根据阿里云百炼要求，优先转换为WAV格式
      let processedBlob = audioBlob
      let fileExtension = 'wav'
      
      if (mimeType.includes('webm') || mimeType.includes('ogg')) {
        // 将WebM/OGG转换为WAV格式
        try {
          processedBlob = await convertToWav(audioBlob)
          fileExtension = 'wav'
          console.log('已转换为WAV格式')
        } catch (error) {
          console.error('转换WAV失败，使用原始格式:', error)
          fileExtension = mimeType.includes('webm') ? 'webm' : 'ogg'
        }
      } else if (mimeType.includes('pcm')) {
        fileExtension = 'pcm'
      } else if (mimeType.includes('mp3')) {
        fileExtension = 'mp3'
      } else if (mimeType.includes('aac')) {
        fileExtension = 'aac'
      }
      
      await handleVoiceMessage(processedBlob, fileExtension)
    }
    
    // 开始监听
    callState.value = 'listening'
    startVoiceDetection()
    
  } catch (error) {
    console.error('无法访问麦克风:', error)
    alert('无法访问麦克风，请检查权限设置')
    callState.value = 'idle'
  }
}

// 结束通话
const endCall = () => {
  console.log('结束通话')
  cleanup()
  emit('close')
}

// 关闭弹框（结束通话并关闭弹框）
const closeModal = () => {
  try {
    // 先结束通话
    endCall()
  } catch (error) {
    console.error('关闭弹框时出错:', error)
    // 即使出错也要关闭弹框
    emit('close')
  }
}

// 开始语音检测
const startVoiceDetection = () => {
  console.log('开始语音检测')
  
  const detectVoice = () => {
    // 检查状态
    if (callState.value !== 'listening' && callState.value !== 'recording') {
      console.log('语音检测暂停：状态不是监听或录音，当前状态:', callState.value)
      // 继续检测，等待状态变为listening
      animationId = requestAnimationFrame(detectVoice)
      return
    }
    
    // 检查音频分析器
    if (!analyser || !dataArray) {
      console.log('语音检测停止：音频分析器不可用')
      return
    }
    
    // 获取音频数据
    analyser.getByteFrequencyData(dataArray)
    
    // 计算平均音量
    let sum = 0
    for (let i = 0; i < dataArray.length; i++) {
      sum += dataArray[i]
    }
    const average = sum / dataArray.length / 255
    
    // 动态调整噪音水平（在前30秒内）
    if (samplesCount < 3000) { // 约30秒的采样
      samplesCount++
      // 使用移动平均来平滑噪音水平
      noiseLevel = noiseLevel * 0.9 + average * 0.1
    }
    
    // 计算动态阈值
    let dynamicVoiceThreshold, dynamicResumeThreshold
    
    if (samplesCount < 100) {
      // 前100次采样使用固定阈值
      dynamicVoiceThreshold = 0.12  // 高于环境噪音0.09
      dynamicResumeThreshold = 0.15
    } else {
      // 使用动态计算的阈值
      dynamicVoiceThreshold = Math.max(noiseLevel * 1.3, 0.12)
      dynamicResumeThreshold = Math.max(noiseLevel * 1.8, 0.15)
    }
    
    // 每100次检测输出一次调试信息
    if (Math.random() < 0.01) {
      console.log('语音检测状态:', {
        state: callState.value,
        volume: average.toFixed(4),
        noiseLevel: noiseLevel.toFixed(4),
        dynamicVoiceThreshold: dynamicVoiceThreshold.toFixed(4),
        dynamicResumeThreshold: dynamicResumeThreshold.toFixed(4),
        isSilenceCounting: isSilenceCounting,
        hasSilenceTimer: !!silenceTimer,
        samplesCount: samplesCount
      })
    }
    
    // 更新音量波浪条
    updateVolumeBars(dataArray)
    
    // 语音检测逻辑
    if (callState.value === 'listening') {
      if (average > dynamicVoiceThreshold) {
        console.log('检测到语音，开始录音，音量:', average, '阈值:', dynamicVoiceThreshold)
        startRecording()
      }
    } else if (callState.value === 'recording') {
      if (average <= dynamicVoiceThreshold && !isSilenceCounting) {
        // 开始静音计时
        console.log('开始静音计时，音量:', average, '阈值:', dynamicVoiceThreshold)
        isSilenceCounting = true
        silenceTimer = setTimeout(() => {
          console.log('静音超时，停止录音')
          stopRecording()
        }, SILENCE_DURATION)
      } else if (average > dynamicResumeThreshold && isSilenceCounting) {
        // 只有声音足够大时才清除静音计时器
        console.log('检测到足够大的声音，清除静音计时器，音量:', average, '阈值:', dynamicResumeThreshold)
        clearTimeout(silenceTimer)
        silenceTimer = null
        isSilenceCounting = false
      }
    }
    
    // 继续检测
    animationId = requestAnimationFrame(detectVoice)
  }
  
  detectVoice()
}

// 更新音量波浪条
const updateVolumeBars = (frequencyData) => {
  if (!frequencyData) return
  
  const barCount = volumeBars.value.length
  const dataLength = frequencyData.length
  const step = Math.floor(dataLength / barCount)
  
  // 计算每个波浪条的高度
  for (let i = 0; i < barCount; i++) {
    let sum = 0
    const start = i * step
    const end = Math.min(start + step, dataLength)
    
    // 计算该频段的平均值
    for (let j = start; j < end; j++) {
      sum += frequencyData[j]
    }
    const average = sum / (end - start)
    
    // 转换为高度 (4-40px)
    const height = Math.max(4, Math.min(40, (average / 255) * 36 + 4))
    volumeBars.value[i] = height
  }
}


// 播放AI回复音频
const playAiResponse = async (audioUrl) => {
  return new Promise((resolve, reject) => {
    try {
      console.log('开始播放AI回复音频:', audioUrl)
      
      // 使用认证音频URL
      getAuthenticatedAudioUrl(audioUrl)
        .then(authenticatedUrl => {
          const audio = new Audio(authenticatedUrl)
          currentAudio = audio  // 保存音频对象引用
          
          audio.onplay = () => {
            console.log('音频开始播放')
          }
          
          audio.onended = () => {
            console.log('音频播放结束，恢复监听')
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            console.log('状态已设置为listening，语音检测应该恢复')
            resolve()
          }
          
          audio.onerror = (error) => {
            console.error('音频播放失败:', error)
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            reject(error)
          }
          
          // 开始播放
          audio.play().catch(err => {
            console.error('音频播放启动失败:', err)
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            reject(err)
          })
        })
        .catch(error => {
          console.error('获取认证音频失败:', error)
          // 降级到非认证URL
          const authenticatedUrl = convertAudioUrl(audioUrl)
          const audio = new Audio(authenticatedUrl)
          currentAudio = audio  // 保存音频对象引用
          
          audio.onplay = () => {
            console.log('音频开始播放')
          }
          
          audio.onended = () => {
            console.log('音频播放结束，恢复监听')
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            console.log('状态已设置为listening，语音检测应该恢复')
            resolve()
          }
          
          audio.onerror = (error) => {
            console.error('音频播放失败:', error)
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            reject(error)
          }
          
          audio.play().catch(err => {
            console.error('音频播放启动失败:', err)
            currentAudio = null  // 清除引用
            callState.value = 'listening'
            reject(err)
          })
        })
        
    } catch (error) {
      console.error('创建音频对象失败:', error)
      callState.value = 'listening'
      reject(error)
    }
  })
}

// 开始录音
const startRecording = () => {
  if (mediaRecorder && mediaRecorder.state === 'inactive') {
    console.log('开始录音')
    audioChunks = []
    mediaRecorder.start()
    callState.value = 'recording'
  }
}

// 停止录音
const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state === 'recording') {
    console.log('停止录音')
    mediaRecorder.stop()
    callState.value = 'processing'
  }
}



// 转换为符合阿里云百炼要求的WAV格式
const convertToWav = async (audioBlob) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = async () => {
      try {
        const arrayBuffer = reader.result
        const audioContext = new (window.AudioContext || window.webkitAudioContext)()
        const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)
        
        console.log('原始音频信息:', {
          sampleRate: audioBuffer.sampleRate,
          channels: audioBuffer.numberOfChannels,
          length: audioBuffer.length,
          duration: audioBuffer.duration
        })
        
        // 重采样到16000Hz（阿里云百炼推荐采样率）
        let processedBuffer = audioBuffer
        if (audioBuffer.sampleRate !== 16000) {
          console.log('重采样从', audioBuffer.sampleRate, '到16000Hz')
          processedBuffer = await resampleTo16000(audioBuffer)
        }
        
        // 转换为单声道（阿里云百炼推荐）
        if (processedBuffer.numberOfChannels > 1) {
          console.log('转换为单声道')
          processedBuffer = convertToMono(processedBuffer)
        }
        
        // 生成WAV文件
        const wavBlob = createWavBlob(processedBuffer)
        resolve(wavBlob)
        
      } catch (error) {
        console.error('WAV转换失败:', error)
        reject(error)
      }
    }
    reader.onerror = reject
    reader.readAsArrayBuffer(audioBlob)
  })
}

// 重采样到16000Hz
const resampleTo16000 = async (audioBuffer) => {
  const sourceSampleRate = audioBuffer.sampleRate
  const targetSampleRate = 16000
  const ratio = sourceSampleRate / targetSampleRate
  const newLength = Math.floor(audioBuffer.length / ratio)
  
  const offlineContext = new OfflineAudioContext(
    1, // 单声道
    newLength,
    targetSampleRate
  )
  
  const source = offlineContext.createBufferSource()
  source.buffer = audioBuffer
  source.connect(offlineContext.destination)
  source.start()
  
  return await offlineContext.startRendering()
}

// 转换为单声道
const convertToMono = (audioBuffer) => {
  const monoBuffer = audioContext.createBuffer(1, audioBuffer.length, audioBuffer.sampleRate)
  const monoData = monoBuffer.getChannelData(0)
  
  // 将多声道混合为单声道
  for (let i = 0; i < audioBuffer.length; i++) {
    let sum = 0
    for (let channel = 0; channel < audioBuffer.numberOfChannels; channel++) {
      sum += audioBuffer.getChannelData(channel)[i]
    }
    monoData[i] = sum / audioBuffer.numberOfChannels
  }
  
  return monoBuffer
}

// 创建符合阿里云百炼要求的WAV Blob
const createWavBlob = (audioBuffer) => {
  const sampleRate = audioBuffer.sampleRate
  const length = audioBuffer.length
  const numberOfChannels = audioBuffer.numberOfChannels
  
  console.log('生成WAV文件:', {
    sampleRate,
    channels: numberOfChannels,
    length,
    duration: length / sampleRate
  })
  
  // 创建WAV文件头（44字节）
  const buffer = new ArrayBuffer(44 + length * numberOfChannels * 2)
  const view = new DataView(buffer)
  
  // WAV文件头
  const writeString = (offset, string) => {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i))
    }
  }
  
  // RIFF header
  writeString(0, 'RIFF')
  view.setUint32(4, 36 + length * numberOfChannels * 2, true)
  writeString(8, 'WAVE')
  
  // fmt chunk
  writeString(12, 'fmt ')
  view.setUint32(16, 16, true) // fmt chunk size
  view.setUint16(20, 1, true)  // audio format (PCM)
  view.setUint16(22, numberOfChannels, true) // number of channels
  view.setUint32(24, sampleRate, true) // sample rate
  view.setUint32(28, sampleRate * numberOfChannels * 2, true) // byte rate
  view.setUint16(32, numberOfChannels * 2, true) // block align
  view.setUint16(34, 16, true) // bits per sample
  
  // data chunk
  writeString(36, 'data')
  view.setUint32(40, length * numberOfChannels * 2, true) // data size
  
  // 写入音频数据（16位PCM）
  let offset = 44
  for (let i = 0; i < length; i++) {
    for (let channel = 0; channel < numberOfChannels; channel++) {
      const sample = Math.max(-1, Math.min(1, audioBuffer.getChannelData(channel)[i]))
      const intSample = sample < 0 ? sample * 0x8000 : sample * 0x7FFF
      view.setInt16(offset, intSample, true)
      offset += 2
    }
  }
  
  console.log('WAV文件大小:', buffer.byteLength, 'bytes')
  return new Blob([buffer], { type: 'audio/wav' })
}

// 处理语音消息
const handleVoiceMessage = async (audioBlob, fileExtension = 'wav') => {
  console.log('处理语音消息，文件大小:', audioBlob.size, '格式:', fileExtension)
  
  if (audioBlob.size < 100) {
    console.log('音频文件太小，跳过处理')
    callState.value = 'listening'
    return
  }
  
  // 发送请求后停止监听
  callState.value = 'processing'
  console.log('开始处理语音，停止监听')
  
  try {
    // 调用语音聊天API
    const formData = new FormData()
    formData.append('audio', audioBlob, `voice.${fileExtension}`)
    formData.append('memoryId', props.memoryId)
    formData.append('characterId', props.characterId)
    
    if (props.knowledgeId) {
      formData.append('knowledgeId', props.knowledgeId)
    }
    
    // 调用语音聊天服务
    const { sendVoiceCharacterMessage } = await import('@/services/characterService')
    const response = await sendVoiceCharacterMessage(formData)
    
    console.log('语音处理响应:', response.data)
    
    // 将用户消息发送到主聊天界面
    if (response.data.transcribedText) {
      emit('add-message', {
        type: 'user',
        content: response.data.transcribedText,
        timestamp: Date.now()
      })
    }
    
    // 将AI回复发送到主聊天界面
    if (response.data.reply) {
      emit('add-message', {
        type: 'ai',
        content: response.data.reply,
        timestamp: Date.now()
      })
    }
    
    // 保存到对话历史中（用于字幕显示）
    if (response.data.transcribedText && response.data.reply) {
      conversationHistory.value.push({
        userText: response.data.transcribedText,
        aiText: response.data.reply,
        audioUrl: response.data.audioUrl,
        timestamp: Date.now()
      })
    }
    
    // 播放AI回复音频
    if (response.data.audioUrl) {
      currentAudioUrl.value = response.data.audioUrl
      callState.value = 'playing'
      await playAiResponse(response.data.audioUrl)
    } else {
      // 没有音频回复，直接恢复监听
      callState.value = 'listening'
    }
    
  } catch (error) {
    console.error('语音处理失败:', error)
    
    // 将错误消息发送到主聊天界面
    emit('add-message', {
      type: 'ai',
      content: '抱歉，处理您的语音时出现了错误',
      timestamp: Date.now()
    })
    
    // 恢复监听
    callState.value = 'listening'
  }
}


// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit', 
    second: '2-digit' 
  })
}


// 获取缓存的头像URL（同步）
const getCachedAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) return ''
  
  // 如果已经缓存了，直接返回
  if (avatarCache.value.has(avatarUrl)) {
    return avatarCache.value.get(avatarUrl)
  }
  
  // 如果还没缓存，先返回降级URL，同时异步加载认证URL
  const fallbackUrl = convertImageUrl(avatarUrl)
  avatarCache.value.set(avatarUrl, fallbackUrl)
  
  // 异步加载认证URL
  loadAuthenticatedAvatarUrl(avatarUrl)
  
  return fallbackUrl
}

// 异步加载认证头像URL
const loadAuthenticatedAvatarUrl = async (avatarUrl) => {
  try {
    const authenticatedUrl = await getAuthenticatedImageUrl(avatarUrl)
    avatarCache.value.set(avatarUrl, authenticatedUrl)
    // 如果是当前角色的头像，更新显示
    if (avatarUrl === props.characterAvatar) {
      authenticatedAvatarUrl.value = authenticatedUrl
    }
  } catch (error) {
    console.error('获取认证头像失败:', error)
    // 保持降级URL
  }
}

// 加载认证头像
const loadAuthenticatedAvatar = async () => {
  if (props.characterAvatar) {
    // 先尝试从缓存获取
    const cachedUrl = getCachedAvatarUrl(props.characterAvatar)
    if (cachedUrl && cachedUrl !== convertImageUrl(props.characterAvatar)) {
      // 缓存中有认证URL，直接使用
      authenticatedAvatarUrl.value = cachedUrl
    } else {
      // 缓存中没有，异步加载
      try {
        authenticatedAvatarUrl.value = await getAuthenticatedImageUrl(props.characterAvatar)
        avatarCache.value.set(props.characterAvatar, authenticatedAvatarUrl.value)
      } catch (error) {
        console.error('加载认证头像失败:', error)
        authenticatedAvatarUrl.value = convertImageUrl(props.characterAvatar)
        avatarCache.value.set(props.characterAvatar, authenticatedAvatarUrl.value)
      }
    }
  }
}

// 监听弹窗显示/关闭
watch(() => props.show, (newVal) => {
  if (newVal) {
    // 弹窗显示时加载头像并自动开始通话
    loadAuthenticatedAvatar()
    startCall()
  } else {
    // 弹窗关闭时清理资源
    cleanup()
  }
})

// 监听头像变化
watch(() => props.characterAvatar, () => {
  loadAuthenticatedAvatar()
})
</script>

<style scoped>
@keyframes scaleIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-scale-in {
  animation: scaleIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
</style>
