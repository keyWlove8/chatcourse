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
        <div v-if="isCallActive" class="flex justify-center items-center space-x-1 mb-4">
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
          <div v-if="isCallActive && isProcessing" class="flex items-center justify-center gap-2 text-blue-500">
            <i class="fa fa-spinner fa-spin"></i>
            <span class="text-sm font-medium">正在处理...</span>
          </div>
          <div v-else-if="isCallActive && isWaitingForResponse" class="flex items-center justify-center gap-2 text-yellow-500">
            <i class="fa fa-clock"></i>
            <span class="text-sm font-medium">正在回复...</span>
          </div>
          <div v-else-if="isCallActive" class="flex items-center justify-center gap-2 text-green-500">
            <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
            <span class="text-sm font-medium">通话中...</span>
          </div>
          <div v-else class="flex items-center justify-center gap-2 text-gray-500">
            <i class="fa fa-phone"></i>
            <span class="text-sm font-medium">点击开始通话</span>
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
            v-if="isCallActive"
            @click="showSubtitleModal = true"
            class="w-12 h-12 rounded-full bg-blue-500 hover:bg-blue-600 text-white flex items-center justify-center transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
            title="查看字幕"
          >
            <i class="fa fa-closed-captioning"></i>
          </button>

          <!-- 开始/结束通话按钮 -->
          <button
            v-if="!isCallActive"
            @click="startCall"
            class="w-16 h-16 rounded-full bg-green-500 hover:bg-green-600 text-white flex items-center justify-center transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
          >
            <i class="fa fa-phone"></i>
          </button>

          <!-- 挂断按钮 -->
          <button
            v-if="isCallActive"
            @click="endCall"
            class="w-16 h-16 rounded-full bg-red-500 hover:bg-red-600 text-white flex items-center justify-center transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg"
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

        <!-- 当前处理状态 -->
        <div v-if="isProcessing" class="mt-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
          <div class="flex items-center gap-2 text-yellow-700 dark:text-yellow-300">
            <i class="fa fa-spinner fa-spin"></i>
            <span class="text-sm">正在处理您的语音...</span>
          </div>
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
                <div class="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center flex-shrink-0">
                  <i class="fa fa-robot text-white text-sm"></i>
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

const isRecording = ref(false)
const isProcessing = ref(false)
const isPlaying = ref(false)  // 是否正在播放AI回复
const transcribedText = ref('')
const aiReply = ref('')
const mediaRecorder = ref(null)
const audioChunks = ref([])
const currentAudioUrl = ref('')
const authenticatedAvatarUrl = ref('')

// 字幕相关状态
const showSubtitleModal = ref(false)
const conversationHistory = ref([])  // 存储对话历史

// 通话模式相关状态
const isCallActive = ref(false)  // 通话是否激活
const isListening = ref(false)   // 是否正在监听
const isWaitingForResponse = ref(false)  // 是否在等待AI回复
const showWaitingMessage = ref(false)  // 是否显示等待提示
const silenceTimer = ref(null)   // 静音计时器
const audioContext = ref(null)   // 音频上下文
const analyser = ref(null)       // 音频分析器
const dataArray = ref(null)      // 音频数据数组
const animationId = ref(null)    // 动画ID
const volumeBars = ref(new Array(12).fill(0))  // 音量波浪条

// 语音检测参数
const SILENCE_THRESHOLD = 0.01   // 静音阈值
const SILENCE_DURATION = 1000    // 静音持续时间(ms)
const VOICE_THRESHOLD = 0.15     // 语音阈值（进一步提高阈值，减少误触发）

// 清理通话状态（不关闭弹框）
const cleanupCall = () => {
  try {
    isCallActive.value = false
    isListening.value = false
    isWaitingForResponse.value = false
    isPlaying.value = false
    showWaitingMessage.value = false
    showSubtitleModal.value = false  // 关闭字幕弹框
    
    // 清理定时器
    if (silenceTimer.value) {
      clearTimeout(silenceTimer.value)
      silenceTimer.value = null
    }
    
    // 清理动画
    if (animationId.value) {
      cancelAnimationFrame(animationId.value)
      animationId.value = null
    }
    
    // 重置音量波浪条
    volumeBars.value = new Array(12).fill(0)
    
    // 停止录音
    if (mediaRecorder.value && isRecording.value) {
      try {
        mediaRecorder.value.stop()
        isRecording.value = false
      } catch (error) {
        console.warn('停止录音时出错:', error)
      }
    }
    
    // 停止音频轨道
    if (mediaRecorder.value && mediaRecorder.value.stream) {
      try {
        mediaRecorder.value.stream.getTracks().forEach(track => {
          track.stop()
        })
      } catch (error) {
        console.warn('停止音频轨道时出错:', error)
      }
    }
    
    // 关闭音频上下文
    if (audioContext.value && audioContext.value.state !== 'closed') {
      try {
        audioContext.value.close()
      } catch (error) {
        console.warn('关闭音频上下文时出错:', error)
      }
    }
    
    // 清理引用
    mediaRecorder.value = null
    audioContext.value = null
    analyser.value = null
    dataArray.value = null
  } catch (error) {
    console.error('清理通话状态时出错:', error)
  }
}

// 开始通话
const startCall = async () => {
  try {
    // 先清理之前的状态（不关闭弹框）
    cleanupCall()
    
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    
    // 设置音频上下文用于语音检测
    audioContext.value = new (window.AudioContext || window.webkitAudioContext)()
    const source = audioContext.value.createMediaStreamSource(stream)
    analyser.value = audioContext.value.createAnalyser()
    analyser.value.fftSize = 256
    source.connect(analyser.value)
    
    const bufferLength = analyser.value.frequencyBinCount
    dataArray.value = new Uint8Array(bufferLength)
    
    // 设置录音器
    mediaRecorder.value = new MediaRecorder(stream, {
      mimeType: 'audio/webm;codecs=opus'
    })
    audioChunks.value = []

    mediaRecorder.value.ondataavailable = (event) => {
      audioChunks.value.push(event.data)
    }

    mediaRecorder.value.onstop = () => {
      const audioBlob = new Blob(audioChunks.value, { type: 'audio/webm' })
      handleVoiceMessage(audioBlob)
    }

    // 开始通话
    isCallActive.value = true
    isListening.value = true
    startVoiceDetection()
    
  } catch (error) {
    console.error('无法访问麦克风:', error)
    alert('无法访问麦克风，请检查权限设置')
    // 确保状态被重置
    isCallActive.value = false
    isListening.value = false
  }
}

// 结束通话
const endCall = () => {
  try {
    isCallActive.value = false
    isListening.value = false
    isWaitingForResponse.value = false
    isPlaying.value = false
    showWaitingMessage.value = false
    showSubtitleModal.value = false  // 关闭字幕弹框
    
    // 清理定时器
    if (silenceTimer.value) {
      clearTimeout(silenceTimer.value)
      silenceTimer.value = null
    }
    
    // 清理动画
    if (animationId.value) {
      cancelAnimationFrame(animationId.value)
      animationId.value = null
    }
    
    // 停止录音
    if (mediaRecorder.value && isRecording.value) {
      try {
        mediaRecorder.value.stop()
        isRecording.value = false
      } catch (error) {
        console.warn('停止录音时出错:', error)
      }
    }
    
    // 停止音频轨道
    if (mediaRecorder.value && mediaRecorder.value.stream) {
      try {
        mediaRecorder.value.stream.getTracks().forEach(track => {
          track.stop()
        })
      } catch (error) {
        console.warn('停止音频轨道时出错:', error)
      }
    }
    
    // 关闭音频上下文
    if (audioContext.value && audioContext.value.state !== 'closed') {
      try {
        audioContext.value.close()
      } catch (error) {
        console.warn('关闭音频上下文时出错:', error)
      }
    }
    
    // 清理引用
    mediaRecorder.value = null
    audioContext.value = null
    analyser.value = null
    dataArray.value = null
    
    emit('close')
  } catch (error) {
    console.error('结束通话时出错:', error)
    // 即使出错也要关闭弹框
    emit('close')
  }
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
  const detectVoice = () => {
    if (!isCallActive.value) return
    
    analyser.value.getByteFrequencyData(dataArray.value)
    
    // 计算平均音量
    let sum = 0
    for (let i = 0; i < dataArray.value.length; i++) {
      sum += dataArray.value[i]
    }
    const average = sum / dataArray.value.length / 255
    
    // 更新音量波浪条
    updateVolumeBars(dataArray.value)
    
    if (isListening.value && !isWaitingForResponse.value && !isProcessing.value) {
      // 检测到语音
      if (average > VOICE_THRESHOLD) {
        if (!isRecording.value) {
          startRecording()
        }
        // 重置静音计时器
        if (silenceTimer.value) {
          clearTimeout(silenceTimer.value)
        }
        silenceTimer.value = setTimeout(() => {
          if (isRecording.value) {
            stopRecording()
          }
        }, SILENCE_DURATION)
      } else {
        // 没有检测到语音，如果正在录音，启动静音计时器
        if (isRecording.value && !silenceTimer.value) {
          silenceTimer.value = setTimeout(() => {
            if (isRecording.value) {
              stopRecording()
            }
          }, SILENCE_DURATION)
        }
      }
    } else if (isWaitingForResponse.value || isProcessing.value) {
      // 等待回复期间检测到语音，提示用户
      if (average > VOICE_THRESHOLD) {
        // 显示提示给用户
        showWaitingPrompt()
      }
    }
    
    animationId.value = requestAnimationFrame(detectVoice)
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

// 显示等待提示
const showWaitingPrompt = () => {
  showWaitingMessage.value = true
  // 3秒后自动隐藏提示
  setTimeout(() => {
    showWaitingMessage.value = false
  }, 3000)
}

// 播放AI回复音频
const playAiResponse = async (audioUrl) => {
  try {
    // 使用认证音频URL
    let authenticatedUrl
    try {
      authenticatedUrl = await getAuthenticatedAudioUrl(audioUrl)
    } catch (error) {
      console.error('获取认证音频失败:', error)
      // 降级到非认证URL
      authenticatedUrl = convertAudioUrl(audioUrl)
    }
    
    // 创建音频对象
    const audio = new Audio(authenticatedUrl)
    
    // 设置播放事件监听
    audio.onloadstart = () => {
      console.log('开始加载音频...')
    }
    
    audio.oncanplay = () => {
      console.log('音频可以播放')
    }
    
    audio.onplay = () => {
      console.log('开始播放AI回复音频')
      isPlaying.value = true
    }
    
    audio.onended = () => {
      console.log('AI回复音频播放完成')
      isPlaying.value = false
      isWaitingForResponse.value = false
      isListening.value = true  // 恢复监听
      showWaitingMessage.value = false  // 隐藏等待提示
      // 清理静音计时器，准备下一轮录音
      if (silenceTimer.value) {
        clearTimeout(silenceTimer.value)
        silenceTimer.value = null
      }
    }
    
    audio.onerror = (error) => {
      console.error('音频播放失败:', error)
      isPlaying.value = false
      // 即使播放失败也要恢复监听
      isWaitingForResponse.value = false
      isListening.value = true
      showWaitingMessage.value = false
      // 清理静音计时器，准备下一轮录音
      if (silenceTimer.value) {
        clearTimeout(silenceTimer.value)
        silenceTimer.value = null
      }
    }
    
    // 开始播放
    audio.play().catch(err => {
      console.error('音频播放启动失败:', err)
      isPlaying.value = false
      isWaitingForResponse.value = false
      isListening.value = true
      showWaitingMessage.value = false
      // 清理静音计时器，准备下一轮录音
      if (silenceTimer.value) {
        clearTimeout(silenceTimer.value)
        silenceTimer.value = null
      }
    })
    
  } catch (error) {
    console.error('创建音频对象失败:', error)
    isWaitingForResponse.value = false
    isListening.value = true
    showWaitingMessage.value = false
    // 清理静音计时器，准备下一轮录音
    if (silenceTimer.value) {
      clearTimeout(silenceTimer.value)
      silenceTimer.value = null
    }
  }
}

// 开始录音
const startRecording = () => {
  if (mediaRecorder.value && !isRecording.value) {
    audioChunks.value = []
    mediaRecorder.value.start()
    isRecording.value = true
  }
}

// 停止录音
const stopRecording = () => {
  if (mediaRecorder.value && isRecording.value) {
    mediaRecorder.value.stop()
    isRecording.value = false
  }
}

// 处理语音消息
const handleVoiceMessage = async (audioBlob) => {
  // 检查音频是否太短（少于100字节）
  if (audioBlob.size < 100) {
    return
  }
  
  isProcessing.value = true
  isWaitingForResponse.value = true
  isListening.value = false  // 停止监听，等待处理完成
  
  try {
    // 调用语音聊天API
    const formData = new FormData()
    formData.append('audio', audioBlob, 'voice.webm')
    formData.append('memoryId', props.memoryId)
    formData.append('characterId', props.characterId)
    
    
    if (props.knowledgeId) {
      formData.append('knowledgeId', props.knowledgeId)
    }
    
    // 调用语音聊天服务
    const { sendVoiceCharacterMessage } = await import('@/services/characterService')
    const response = await sendVoiceCharacterMessage(formData)
    
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
    
    // 在语音弹窗中播放AI回复的音频
    if (response.data.audioUrl) {
      currentAudioUrl.value = response.data.audioUrl
      
      // 播放AI回复音频
      playAiResponse(response.data.audioUrl)
    } else {
      // 没有音频回复，直接恢复监听
      isWaitingForResponse.value = false
      isListening.value = true
      showWaitingMessage.value = false  // 隐藏等待提示
    }
    
    isProcessing.value = false
    
  } catch (error) {
    console.error('语音处理失败:', error)
    
    // 将错误消息发送到主聊天界面
    emit('add-message', {
      type: 'ai',
      content: '抱歉，处理您的语音时出现了错误',
      timestamp: Date.now()
    })
    
    // 恢复监听
    isProcessing.value = false
    isWaitingForResponse.value = false
    isListening.value = true
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


// 加载认证头像
const loadAuthenticatedAvatar = async () => {
  if (props.characterAvatar) {
    try {
      authenticatedAvatarUrl.value = await getAuthenticatedImageUrl(props.characterAvatar)
    } catch (error) {
      console.error('加载认证头像失败:', error)
      authenticatedAvatarUrl.value = convertImageUrl(props.characterAvatar)
    }
  }
}

// 监听弹窗显示/关闭
watch(() => props.show, (newVal) => {
  if (newVal) {
    // 弹窗显示时加载头像并启动通话
    loadAuthenticatedAvatar()
    startCall()
  } else {
    // 弹窗关闭时结束通话
    endCall()
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
