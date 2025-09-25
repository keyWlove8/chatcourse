<template>
  <div class="min-h-screen flex flex-col bg-white dark:bg-gray-950 text-gray-900 dark:text-gray-100">
    <!-- 顶部导航 -->
    <header class="border-b border-slate-200 dark:border-slate-700 bg-white/90 dark:bg-slate-900/90 backdrop-blur-md">
      <div class="container mx-auto px-6 py-4 flex justify-between items-center">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center shadow-sm overflow-hidden">
            <img src="/kunkun.jpg" alt="AiChat" class="w-full h-full object-cover rounded-lg" />
          </div>
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">AiChat</h1>
            <p class="text-sm text-gray-500 dark:text-gray-400">守护最好的kunkun</p>
          </div>
        </div>
        
        <div class="flex items-center gap-4">
          <!-- 角色选择器 -->
          <div class="hidden md:block">
            <CharacterSelector />
          </div>
          
          <!-- 角色管理按钮 -->
          <button 
            @click="showCharacterManagement = true"
            class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl font-medium text-sm"
            title="角色管理"
          >
            <i class="fa fa-users text-sm"></i>
            <span class="hidden sm:inline">角色管理</span>
          </button>
          
          <!-- 音色管理按钮 (管理员专用) -->
          <PermissionGuard permission="isAdmin">
            <button 
              @click="showVoiceManagement = true"
              class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl font-medium text-sm"
              title="音色管理"
            >
              <i class="fa fa-microphone text-sm"></i>
              <span class="hidden sm:inline">音色管理</span>
            </button>
          </PermissionGuard>
          
          <!-- 登录状态 -->
          <div v-if="authStore.isLogin" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
            <div class="w-2 h-2 bg-emerald-500 rounded-full"></div>
            <span class="font-medium text-slate-900 dark:text-slate-100">
              {{ isUserInfoLoaded ? authStore.userInfo.username : '加载中...' }}
            </span>
            <span class="text-xs text-blue-600 dark:text-blue-400">
              {{ authStore.userInfo.isAdmin ? 'Admin' : 'User' }}
            </span>
          </div>
          
          <!-- 暗黑模式切换 -->
          <button 
            @click="toggleDarkMode" 
            class="p-2 bg-gradient-to-r from-slate-100 to-slate-200 dark:from-slate-800 dark:to-slate-700 hover:from-slate-200 hover:to-slate-300 dark:hover:from-slate-700 dark:hover:to-slate-600 rounded-lg transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-md hover:shadow-lg"
            :title="darkMode ? '切换到亮色模式' : '切换到暗色模式'"
          >
            <i class="text-lg transition-all duration-300" :class="darkMode ? 'fas fa-sun text-amber-500' : 'fas fa-moon text-slate-600 dark:text-slate-400'"></i>
          </button>
          
          <!-- 退出登录 -->
          <button 
            v-if="authStore.isLogin" 
            @click="authStore.logout"
            class="px-3 py-1.5 bg-gradient-to-r from-rose-500 to-red-500 hover:from-rose-600 hover:to-red-600 text-white rounded-lg text-sm font-medium transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-md hover:shadow-lg"
          >
            <i class="fa fa-sign-out mr-1"></i>退出
          </button>
        </div>
      </div>
    </header>

                <!-- 主内容区 -->
     <main class="container mx-auto px-4 sm:px-6 py-8 flex-1 flex flex-col md:flex-row gap-8 min-h-0">
             <!-- 左侧知识库列表 - 使用侧边栏控制组件 -->
               <SidebarToggle ref="sidebarToggle">
          <div class="w-full h-full bg-white/95 dark:bg-slate-900/95 rounded-xl border border-slate-200 dark:border-slate-700 shadow-lg p-4 sm:p-6 flex flex-col md:w-72 lg:w-80 min-h-0 overflow-hidden">
          <!-- 切换按钮区域 -->
          <div class="flex gap-2 mb-6">
            <button 
              @click="activeTab = 'knowledge'"
              class="flex-1 px-3 py-2 text-sm sm:text-base font-medium rounded-lg transition-all duration-200 flex items-center justify-center gap-2"
                              :class="activeTab === 'knowledge' 
                ? 'bg-blue-600 text-white shadow-md' 
                : 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'"
            >
              <i class="fa fa-database text-sm sm:text-base"></i>
              <span class="hidden sm:inline">知识库</span>
            </button>
            <button 
              @click="activeTab = 'history'"
              class="flex-1 px-3 py-2 text-sm sm:text-base font-medium rounded-lg transition-all duration-200 flex items-center justify-center gap-2"
              :class="activeTab === 'history' 
                ? 'bg-blue-600 text-white shadow-md' 
                : 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'"
            >
              <i class="fa fa-comments text-sm sm:text-base"></i>
              <span class="hidden sm:inline">历史</span>
            </button>
          </div>

          <!-- 知识库选择区域 -->
          <div v-if="activeTab === 'knowledge'" class="flex-1 flex flex-col min-h-0">
            <div class="flex justify-between items-center mb-4">
              <div class="flex items-center gap-2">
                <div class="w-6 h-6 bg-blue-600 rounded-lg flex items-center justify-center">
                  <i class="fa fa-database text-white text-xs"></i>
                </div>
                <div>
                  <h3 class="text-sm sm:text-base font-semibold text-gray-900 dark:text-white">知识库管理</h3>
                  <p class="text-xs text-gray-500 dark:text-gray-400">管理您的知识资源</p>
                </div>
              </div>
              <!-- 管理员：新建按钮 -->
              <PermissionGuard permission="isAdmin">
                <button 
                  @click="showCreateModal = true"
                  class="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200 font-medium"
                  title="创建新知识库"
                >
                  <i class="fa fa-plus text-sm"></i>
                  <span class="text-sm">新建知识库</span>
                </button>
              </PermissionGuard>
            </div>
            
            <!-- 加载状态 -->
            <div v-if="knowledgeStore.isLoading" class="text-center py-8">
              <div class="relative">
                <div class="w-12 h-12 border-4 border-primary-200 dark:border-primary-700 rounded-full animate-spin mx-auto mb-3"></div>
                <div class="absolute inset-0 w-12 h-12 border-4 border-transparent border-t-primary-500 rounded-full animate-spin mx-auto"></div>
              </div>
              <p class="text-sm text-gray-600 dark:text-gray-400 font-medium">加载中<span class="loading-dots"></span></p>
            </div>
            
                         <!-- 知识库列表 -->
             <div v-else class="space-y-3 flex-1 overflow-y-auto min-h-0 sidebar-content-scroll" style="max-height: 200px;">
              <!-- 知识库选择提示 -->
              <div class="bg-gray-50 dark:bg-gray-800 rounded-lg p-3 border border-gray-200 dark:border-gray-700">
                <div class="flex items-center gap-2 mb-2">
                  <i class="fa fa-database text-blue-600 text-sm"></i>
                  <span class="text-sm font-medium text-gray-700 dark:text-gray-300">选择知识库</span>
                </div>
                
                <!-- 知识库下拉选择框 -->
                <div class="relative">
                  <select 
                    v-model="knowledgeStore.selectedId"
                    @change="handleKnowledgeChange"
                    class="w-full p-2 sm:p-3 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 text-sm sm:text-base"
                  >
                    <option value="">请选择知识库</option>
                    <option 
                      v-for="item in knowledgeStore.knowledgeList" 
                      :key="item.id"
                      :value="item.id"
                    >
                      {{ item.name }}
                    </option>
                  </select>
                  
                  <!-- 当前选中的知识库信息 -->
                  <div v-if="knowledgeStore.selectedId" class="mt-2 p-2 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-700 rounded-lg">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center gap-2">
                        <i class="fa fa-database text-blue-600 text-sm"></i>
                        <div>
                          <div class="text-sm font-medium text-blue-900 dark:text-blue-100">{{ knowledgeStore.selectedKnowledge?.name || '未知' }}</div>
                          <div class="text-xs text-blue-600 dark:text-blue-400">
                            <i class="fa fa-clock mr-1"></i>{{ formatTime(knowledgeStore.selectedKnowledge?.createTime) }}
                          </div>
                        </div>
                      </div>
                      <button 
                        @click="knowledgeStore.clearSelectedId"
                        class="p-1 text-blue-500 hover:text-blue-700 hover:bg-blue-100 dark:hover:bg-blue-800 rounded transition-colors duration-200"
                        title="取消选择"
                      >
                        <i class="fa fa-times text-sm"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div v-if="knowledgeStore.knowledgeList.length === 0" class="text-center py-6">
                <div class="w-10 h-10 bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center mx-auto mb-2">
                  <i class="fa fa-database text-lg text-gray-400"></i>
                </div>
                <p class="text-sm text-gray-500 dark:text-gray-400">暂无知识库</p>
              </div>
            </div>

            <!-- 管理员：上传按钮 -->
            <PermissionGuard permission="isAdmin">
              <button 
                @click="showUploadModal = true"
                class="mt-4 w-full p-2 sm:p-3 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors duration-200 flex-shrink-0 text-sm sm:text-base"
              >
                <i class="fa fa-upload mr-2"></i> 
                上传文件
              </button>
            </PermissionGuard>
          </div>

          <!-- 历史会话区域 -->
          <div v-if="activeTab === 'history'" class="flex-1 flex flex-col min-h-0">
            <div class="flex justify-between items-center mb-4">
              <div class="flex items-center gap-2">
                <div class="w-6 h-6 bg-blue-600 rounded-lg flex items-center justify-center">
                  <i class="fa fa-comments text-white text-xs"></i>
                </div>
                <div>
                  <h3 class="text-sm sm:text-base font-semibold text-gray-900 dark:text-white">历史会话</h3>
                  <p class="text-xs text-gray-500 dark:text-gray-400">查看您的对话记录</p>
                </div>
              </div>
              <button 
                @click="handleCreateNewChat"
                class="p-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="chatStore.messages.length === 0"
                :title="chatStore.messages.length === 0 ? '当前会话没有消息，无法新建会话' : '新建会话'"
              >
                <i class="fa fa-plus text-sm"></i>
              </button>
            </div>
            
            <!-- 加载状态 -->
            <div v-if="chatStore.isLoadingHistory" class="text-center py-6 flex-1 flex items-center justify-center">
              <div class="text-center">
                <div class="w-8 h-8 border-2 border-gray-300 border-t-blue-600 rounded-full animate-spin mx-auto mb-2"></div>
                <p class="text-xs text-gray-500 dark:text-gray-400">加载中...</p>
              </div>
            </div>
            
                         <!-- 历史会话列表 - 独立滚动区域 -->
             <div v-else class="flex-1 overflow-y-auto min-h-0 space-y-2 pr-1 sidebar-content-scroll" style="max-height: 300px;">
              <div 
                v-for="chat in chatStore.chatHistoryList" 
                :key="chat.memoryId"
                class="p-2 sm:p-3 rounded-lg border cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors duration-200"
                :class="chatStore.currentMemoryId === chat.memoryId ? 'border-blue-300 bg-blue-50 dark:bg-blue-900/20' : 'border-gray-200 dark:border-gray-700'"
                @click="chatStore.loadChatHistory(chat.memoryId)"
              >
                <div class="text-gray-600 dark:text-gray-400 text-xs truncate mb-1">
                  <span class="text-blue-600 dark:text-blue-400 font-medium">Q:</span> {{ parseMessageText(chat.lastQuestion, true) || '暂无问题' }}
                </div>
                <div class="text-gray-600 dark:text-gray-400 text-xs truncate mb-2">
                  <span class="text-green-600 dark:text-green-400 font-medium">A:</span> {{ parseMessageText(chat.lastAnswer, false) || '暂无回复' }}
                </div>
                <div class="flex items-center gap-2 text-gray-500 dark:text-gray-400 text-xs">
                  <i class="fa fa-clock text-blue-500"></i>
                  <span>{{ formatTime(chat.lastTime) }}</span>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div v-if="chatStore.chatHistoryList.length === 0" class="text-center py-6">
                <div class="w-10 h-10 bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center mx-auto mb-2">
                  <i class="fa fa-comments text-lg text-gray-400"></i>
                </div>
                <p class="text-xs text-gray-500 dark:text-gray-400">暂无历史会话</p>
              </div>
            </div>
          </div>
        </div>
      </SidebarToggle>

                                                <!-- 右侧聊天区 - 移动端全宽，桌面端自适应 -->
         <div class="flex-1 flex flex-col w-full min-h-0 h-[calc(100vh-180px)] md:h-[calc(100vh-200px)]">
         <!-- 当前知识库信息 -->
         <div v-if="knowledgeStore.selectedId" class="mb-6 p-5 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border border-blue-200 dark:border-blue-700 rounded-xl flex-shrink-0 shadow-sm">
           <div class="flex items-center justify-between">
             <div class="flex items-center gap-3">
               <i class="fa fa-database text-blue-600 text-lg"></i>
               <div>
                 <div class="text-xs text-blue-600 dark:text-blue-400 font-medium">当前知识库</div>
                 <div class="text-sm font-semibold text-blue-900 dark:text-blue-100">{{ knowledgeStore.selectedKnowledge?.name || '未知' }}</div>
               </div>
             </div>
             <button 
               @click="knowledgeStore.clearSelectedId"
               class="p-2 text-blue-500 hover:text-blue-700 hover:bg-blue-100 dark:hover:bg-blue-800/50 rounded transition-colors duration-200"
               title="取消选择"
             >
               <i class="fa fa-times text-sm"></i>
             </button>
           </div>
         </div>

         <!-- 当前角色信息 -->
         <div v-if="characterStore.hasSelectedCharacter" class="mb-6 p-5 bg-gradient-to-r from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 border border-purple-200 dark:border-purple-700 rounded-xl flex-shrink-0 shadow-sm">
           <div class="flex items-center justify-between">
             <div class="flex items-center gap-3">
               <div class="w-8 h-8 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full flex items-center justify-center">
                 <img
                   v-if="characterStore.currentCharacter?.avatarUrl"
                   :src="characterStore.currentCharacter.avatarUrl"
                   :alt="characterStore.currentCharacter.name"
                   class="w-8 h-8 rounded-full object-cover"
                 />
                 <i v-else class="fa fa-user text-white text-sm"></i>
               </div>
               <div>
                 <div class="text-xs text-purple-600 dark:text-purple-400 font-medium">当前角色</div>
                 <div class="text-sm font-semibold text-purple-900 dark:text-purple-100">{{ characterStore.currentCharacter?.name || '未知' }}</div>
               </div>
             </div>
             <button 
               @click="characterStore.clearSelectedCharacter"
               class="p-2 text-purple-500 hover:text-purple-700 hover:bg-purple-100 dark:hover:bg-purple-800/50 rounded transition-colors duration-200"
               title="取消选择"
             >
               <i class="fa fa-times text-sm"></i>
             </button>
           </div>
         </div>
         
                   <!-- 聊天消息列表 - 固定高度，独立滚动 -->
          <div ref="chatMessagesContainer" class="flex-1 overflow-y-auto min-h-0 p-4 bg-gradient-to-b from-gray-50 via-white to-gray-100 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 chat-messages-container max-h-[calc(100vh-240px)] md:max-h-[calc(100vh-280px)] relative">
            <!-- 聊天背景图案 -->
            <div class="absolute inset-0 opacity-5 dark:opacity-10">
              <div class="absolute top-10 left-10 w-32 h-32 bg-blue-500 rounded-full blur-3xl"></div>
              <div class="absolute top-32 right-20 w-24 h-24 bg-purple-500 rounded-full blur-3xl"></div>
              <div class="absolute bottom-20 left-20 w-28 h-28 bg-pink-500 rounded-full blur-3xl"></div>
            </div>
            <!-- 内容层 -->
            <div class="relative z-10">
           <!-- 空状态 - 欢迎界面 -->
           <div v-if="chatStore.messages.length === 0" class="flex flex-col items-center justify-center h-full min-h-[400px] text-center">
             <div class="w-20 h-20 bg-gradient-to-r from-blue-500 to-purple-500 rounded-full flex items-center justify-center mb-6 shadow-lg">
               <i class="fa fa-comments text-white text-3xl"></i>
             </div>
             <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-3">欢迎使用 AiChat</h3>
             <p class="text-gray-600 dark:text-gray-400 mb-6 max-w-md leading-relaxed">
               选择一个角色开始对话，AI将根据角色的性格和背景为您提供个性化的回复
             </p>
             <div class="flex flex-wrap gap-3 justify-center">
               <div class="px-4 py-2 bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300 rounded-full text-sm">
                 <i class="fa fa-user mr-2"></i>选择角色
               </div>
               <div class="px-4 py-2 bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-300 rounded-full text-sm">
                 <i class="fa fa-image mr-2"></i>上传图片
               </div>
               <div class="px-4 py-2 bg-purple-100 dark:bg-purple-900/20 text-purple-700 dark:text-purple-300 rounded-full text-sm">
                 <i class="fa fa-phone mr-2"></i>语音通话
               </div>
             </div>
           </div>
           
           <div
             v-for="message in chatStore.messages"
             :key="message.id"
             class="mb-6"
           >
             
             <!-- 用户消息 -->
             <div
               v-if="message.type === 'user'"
               class="flex justify-end mb-4"
             >
               <div class="flex items-end gap-3 max-w-[70%]">
                 <!-- 消息气泡 -->
                 <div class="bg-blue-500 text-white rounded-2xl rounded-br-md px-4 py-3 shadow-lg relative message-bubble user">
                   <MessageContent :contents="message.contents" />
                   <!-- 时间戳 -->
                   <div class="text-xs text-blue-100 mt-1 opacity-80">
                     {{ formatTime(message.timestamp) }}
                   </div>
                 </div>
                 <!-- 用户头像 -->
                 <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center flex-shrink-0 shadow-md">
                   <i class="fa fa-user text-white text-sm"></i>
                 </div>
               </div>
             </div>
             
             <!-- AI回复 -->
             <div
               v-else-if="message.type === 'ai'"
               class="flex justify-start mb-4"
             >
               <div class="flex items-end gap-3 max-w-[70%]">
                 <!-- AI头像 -->
                 <div class="w-8 h-8 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center flex-shrink-0 shadow-md">
                   <img
                     v-if="characterStore.currentCharacter?.avatarUrl"
                     :src="convertImageUrl(characterStore.currentCharacter.avatarUrl)"
                     :alt="characterStore.currentCharacter.name"
                     class="w-8 h-8 rounded-full object-cover"
                   />
                   <i v-else class="fa fa-robot text-white text-sm"></i>
                 </div>
                 <!-- 消息气泡 -->
                 <div class="bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-2xl rounded-bl-md px-4 py-3 shadow-lg border border-gray-200 dark:border-gray-600 relative message-bubble ai">
                   <MessageContent :contents="message.contents" />
                   <!-- 时间戳 -->
                   <div class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                     {{ formatTime(message.timestamp) }}
                   </div>
                 </div>
               </div>
             </div>
             
             <!-- 系统消息 -->
             <div
               v-else-if="message.type === 'system'"
               class="flex justify-center mb-4"
             >
               <div class="max-w-[60%]">
                 <div 
                   :class="[
                     'rounded-xl px-4 py-3 shadow-sm border text-center',
                     message.isError 
                       ? 'bg-red-50 dark:bg-red-900/20 text-red-800 dark:text-red-200 border-red-200 dark:border-red-700' 
                       : 'bg-gray-50 dark:bg-gray-800 text-gray-800 dark:text-gray-200 border-gray-200 dark:border-gray-700'
                   ]"
                 >
                   <div class="flex items-center justify-center gap-2 mb-2">
                     <i :class="message.isError ? 'fa fa-exclamation-triangle text-red-500' : 'fa fa-info text-gray-500'"></i>
                     <span class="text-xs font-medium">{{ message.isError ? '错误' : '系统消息' }}</span>
                   </div>
                   <MessageContent :contents="message.contents" />
                   
                   <!-- 错误消息显示重试按钮 -->
                   <div v-if="message.isError" class="mt-3">
                     <button 
                       @click="handleRetryMessage"
                       class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg text-sm font-medium transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                       :disabled="chatStore.isSending"
                     >
                       <i v-if="chatStore.isSending" class="fa fa-spinner fa-spin mr-2"></i>
                       <i v-else class="fa fa-redo mr-2"></i>
                       重试
                     </button>
                   </div>
                 </div>
               </div>
             </div>
             
             <!-- 默认消息处理（未知类型） -->
             <div
               v-else
               class="flex justify-center"
             >
               <div class="max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-2xl">
               <div class="flex items-center justify-center gap-2 mb-2">
                 <i class="fa fa-question text-gray-500 text-xs"></i>
                 <span class="text-xs text-gray-500 dark:text-gray-400">未知消息类型: {{ message.type }}</span>
               </div>
               <div class="bg-gray-50 dark:bg-gray-800 text-gray-800 dark:text-gray-100 rounded-lg px-4 py-3 shadow-sm border border-gray-200 dark:border-gray-700">
                 <MessageContent :contents="message.contents" />
               </div>
               </div>
             </div>
           </div>
            </div>
          </div>

                          <!-- 消息输入框 -->
         <div class="mt-4 bg-white/95 dark:bg-slate-900/95 rounded-xl border border-slate-200 dark:border-slate-700 shadow-lg p-3 flex-shrink-0">
           <!-- 移动端角色选择器 -->
           <div class="block md:hidden mb-4">
             <CharacterSelector />
           </div>
           
           <!-- 移动端角色选择提示 -->
           <div v-if="!characterStore.hasSelectedCharacter" class="mb-4 p-4 bg-gradient-to-r from-yellow-50 to-orange-50 dark:from-yellow-900/20 dark:to-orange-900/20 border border-yellow-200 dark:border-yellow-700 rounded-lg">
             <div class="flex items-center gap-3 text-yellow-700 dark:text-yellow-300">
               <i class="fa fa-exclamation-triangle text-yellow-600 text-lg"></i>
               <div>
                 <div class="font-medium">请先选择一个角色</div>
                 <div class="text-sm opacity-90">角色将决定AI的回复风格和行为模式</div>
               </div>
             </div>
           </div>
           <!-- 图片预览区域 -->
           <div v-if="selectedImages.length > 0" class="mb-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-700 rounded-lg">
             <div class="flex items-center justify-between mb-3">
               <div class="flex items-center gap-2">
                 <i class="fa fa-image text-yellow-600 text-sm"></i>
                 <span class="text-sm font-medium text-yellow-700 dark:text-yellow-300">已选择的图片</span>
               </div>
               <button 
                 @click="clearSelectedImages" 
                 class="p-1 text-red-500 hover:text-red-700 hover:bg-red-100 dark:hover:bg-red-800/50 rounded transition-colors duration-200"
               >
                 <i class="fa fa-times mr-1"></i>删除
               </button>
             </div>
             <div class="flex gap-3">
               <div class="relative">
                 <img 
                   :src="selectedImages[0].preview" 
                   :alt="selectedImages[0].name"
                   class="w-20 h-20 object-cover rounded-lg border border-yellow-200 dark:border-yellow-600 shadow-sm"
                 />
                 <span class="block text-xs text-yellow-600 dark:text-yellow-400 mt-2 truncate w-20 text-center">{{ selectedImages[0].name }}</span>
               </div>
             </div>
           </div>
           
           <!-- 聊天上传错误提示 -->
           <div v-if="chatStore.uploadError" class="mb-4 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-700 rounded-lg">
             <div class="flex items-center gap-2 text-red-700 dark:text-red-300">
               <i class="fa fa-exclamation-triangle text-red-600 text-sm"></i>
               <span class="text-sm font-medium">{{ chatStore.uploadError }}</span>
             </div>
           </div>
           
           <!-- 移动端输入布局 -->
           <div class="block md:hidden space-y-4">
             <textarea 
               v-model="inputText" 
               placeholder="输入您的问题..."
               class="w-full p-3 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none text-base"
               rows="2"
               @keydown.enter.prevent="handleSendMessage"
             ></textarea>
             <div class="flex gap-3">
               <button 
                 @click="triggerImageUpload"
                 class="flex-1 p-4 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-medium"
                 :disabled="chatStore.isSending || isVoiceCalling"
                 title="上传图片"
               >
                 <i class="fa fa-image mr-2"></i>添加图片
               </button>
               <button 
                 @click="startVoiceCall"
                 class="flex-1 p-4 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-medium"
                 :disabled="chatStore.isSending || isVoiceCalling || !characterStore.hasSelectedCharacter"
                 title="语音通话"
               >
                 <i class="fa fa-phone mr-2"></i>语音通话
               </button>
               <button 
                 @click="handleSendMessage"
                 class="flex-1 px-6 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl font-semibold transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                 :disabled="chatStore.isSending || !inputText.trim() || isVoiceCalling || !characterStore.hasSelectedCharacter"
               >
                 <i v-if="chatStore.isSending" class="fa fa-spinner fa-spin mr-2"></i>
                 <i v-else-if="isVoiceCalling" class="fa fa-phone mr-2"></i>
                 <i v-else class="fa fa-paper-plane mr-2"></i>
                 {{ isVoiceCalling ? '通话中' : (!characterStore.hasSelectedCharacter ? '请选择角色' : '发送') }}
               </button>
             </div>
           </div>
           
           <!-- 桌面端输入布局 -->
           <div class="hidden md:flex gap-4">
             <!-- 图片上传按钮 -->
             <button 
               @click="triggerImageUpload"
               class="p-4 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex-shrink-0"
               :disabled="chatStore.isSending || isVoiceCalling"
               title="上传图片 (支持 JPG, PNG, GIF, WebP，最大 10MB)"
             >
               <i class="fa fa-image text-lg"></i>
             </button>

             <!-- 语音通话按钮 -->
             <button 
               @click="startVoiceCall"
               class="p-4 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white rounded-xl transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex-shrink-0"
               :disabled="chatStore.isSending || isVoiceCalling || !characterStore.hasSelectedCharacter"
               title="语音通话"
             >
               <i class="fa fa-phone text-lg"></i>
             </button>
             
             <!-- 隐藏的文件输入框 -->
             <input 
               ref="imageInput"
               type="file" 
               accept="image/*"
               @change="handleImageSelect"
               style="position: absolute; left: -9999px; opacity: 0; pointer-events: none;"
             />
             
             <textarea 
               v-model="inputText" 
               placeholder="输入您的问题..."
               class="flex-1 p-3 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 text-gray-900 dark:text-gray-100 resize-none text-base"
               rows="2"
               @keydown.enter.prevent="handleSendMessage"
               :disabled="isVoiceCalling"
             ></textarea>
             <button 
               @click="handleSendMessage"
               class="px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl font-semibold transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
               :disabled="chatStore.isSending || !inputText.trim() || isVoiceCalling || !characterStore.hasSelectedCharacter"
             >
               <i v-if="chatStore.isSending" class="fa fa-spinner fa-spin mr-2"></i>
               <i v-else-if="isVoiceCalling" class="fa fa-phone mr-2"></i>
               <i v-else class="fa fa-paper-plane mr-2"></i>
               {{ isVoiceCalling ? '通话中' : (!characterStore.hasSelectedCharacter ? '请选择角色' : '发送') }}
             </button>
           </div>
         </div>
       </div>
    </main>

    <!-- 创建知识库弹窗 -->
    <div v-if="showCreateModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="card p-8 w-full max-w-md animate-scale-in">
        <div class="text-center mb-6">
          <div class="w-16 h-16 bg-gradient-to-r from-success-400 to-success-500 rounded-full flex items-center justify-center mx-auto mb-4 shadow-glow-success">
            <i class="fa fa-database text-2xl text-white"></i>
          </div>
          <h3 class="text-2xl font-bold text-gradient">创建知识库</h3>
        </div>
        
        <!-- 创建成功提示 -->
        <div v-if="createSuccess" class="mb-6 p-4 bg-gradient-to-r from-success-50 to-green-50 dark:from-success-900/20 dark:to-green-900/20 border border-success-200 dark:border-success-700 rounded-xl">
          <div class="flex items-center text-success-700 dark:text-success-300 mb-3">
            <i class="fa fa-check-circle mr-3 text-xl"></i>
            <span class="text-lg font-semibold">知识库创建成功！</span>
          </div>
          <!-- 显示返回的KnowledgeVO信息 -->
          <div v-if="newCreatedKnowledge" class="text-sm text-success-600 dark:text-success-400 space-y-2">
            <div class="flex justify-between">
              <span class="font-medium">知识库名称：</span>
              <span>{{ newCreatedKnowledge.name }}</span>
            </div>
            <div class="flex justify-between">
              <span class="font-medium">知识库ID：</span>
              <span class="font-mono">{{ newCreatedKnowledge.id }}</span>
            </div>
            <div class="flex justify-between">
              <span class="font-medium">创建时间：</span>
              <span>{{ formatTime(newCreatedKnowledge.createTime) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 创建表单 -->
        <div v-if="!createSuccess">
          <input 
            v-model="newKnowledgeName" 
            placeholder="输入知识库名称"
            class="input-field mb-6"
            :disabled="isCreating"
            @keydown.enter.prevent="!isCreating && handleCreateKnowledge()"
          >
          
          <!-- 创建错误提示 -->
          <div v-if="createError" class="mb-6 p-4 bg-gradient-to-r from-error-50 to-red-50 dark:from-error-900/20 dark:to-red-900/20 border border-error-200 dark:border-error-700 rounded-xl">
            <div class="flex items-center text-error-700 dark:text-error-300">
              <i class="fa fa-exclamation-triangle mr-3 text-lg"></i>
              <span class="text-sm font-medium">{{ createError }}</span>
            </div>
          </div>
          
          <div class="flex justify-end gap-3">
            <button 
              @click="closeCreateModal" 
              class="btn-secondary"
              :disabled="isCreating"
            >
              取消
            </button>
            <button 
              @click="handleCreateKnowledge"
              class="btn-success"
              :disabled="!newKnowledgeName.trim() || isCreating"
            >
              <i v-if="isCreating" class="fa fa-spinner fa-spin mr-2"></i>
              <span>{{ isCreating ? '创建中...' : '创建' }}</span>
            </button>
          </div>
          
          <!-- 创建进度提示 -->
          <div v-if="isCreating" class="mt-6 p-4 bg-gradient-to-r from-primary-50 to-blue-50 dark:from-primary-900/20 dark:to-blue-900/20 border border-primary-200 dark:border-primary-700 rounded-xl">
            <div class="flex items-center text-primary-700 dark:text-primary-300">
              <div class="w-5 h-5 border-2 border-primary-200 border-t-primary-500 rounded-full animate-spin mr-3"></div>
              <span class="text-sm font-medium">正在创建知识库，请稍候<span class="loading-dots"></span></span>
            </div>
          </div>
        </div>
        
        <!-- 成功后的操作按钮 -->
        <div v-else class="flex justify-end">
          <button @click="closeCreateModal" class="btn-success">
            确定
          </button>
        </div>
      </div>
    </div>

    <!-- 上传文件弹窗 -->
    <div v-if="showUploadModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="card p-8 w-full max-w-md animate-scale-in">
        <div class="text-center mb-6">
          <div class="w-16 h-16 bg-gradient-to-r from-success-400 to-success-500 rounded-full flex items-center justify-center mx-auto mb-4 shadow-glow-success">
            <i class="fa fa-upload text-2xl text-white"></i>
          </div>
          <h3 class="text-2xl font-bold text-gradient">上传知识库文件</h3>
        </div>
        
        <!-- 文件格式说明 -->
        <div class="mb-6 p-4 bg-gradient-to-r from-primary-50 to-blue-50 dark:from-primary-900/20 dark:to-blue-900/20 border border-primary-200 dark:border-primary-700 rounded-xl">
          <div class="text-sm text-primary-700 dark:text-primary-300">
            <div class="font-semibold mb-2">支持的文件格式：</div>
            <div class="text-xs space-y-1">
              <div class="flex items-center">
                <i class="fa fa-file-text mr-2 text-primary-500"></i>
                <span>文档：TXT, PDF, DOC, DOCX, XLS, XLSX, CSV</span>
              </div>
              <div class="flex items-center">
                <i class="fa fa-weight-hanging mr-2 text-primary-500"></i>
                <span>文件大小：最大 50MB</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="mb-6">
          <label class="block mb-2 font-medium text-gray-700 dark:text-gray-300">选择知识库</label>
          <select 
            v-model="selectedUploadId" 
            class="input-field"
          >
            <option value="">-- 选择知识库 --</option>
            <option v-for="item in knowledgeStore.knowledgeList" :key="item.id" :value="item.id">{{ item.name }}</option>
          </select>
        </div>
        
        <div class="mb-6">
          <label class="block mb-2 font-medium text-gray-700 dark:text-gray-300">选择文件</label>
          <input 
            type="file" 
            @change="handleFileSelect"
            class="w-full p-3 bg-gradient-to-r from-accent-50 to-yellow-50 dark:from-accent-900/20 dark:to-yellow-900/20 border border-accent-200 dark:border-accent-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-accent-500 focus:border-transparent transition-all duration-200"
            :disabled="knowledgeStore.isLoading"
            accept=".txt,.pdf,.doc,.docx,.xls,.xlsx,.csv"
          >
        </div>
        
        <p v-if="selectedFileName" class="mb-6 p-3 bg-gradient-to-r from-success-50 to-green-50 dark:from-success-900/20 dark:to-green-900/20 border border-success-200 dark:border-success-700 rounded-lg text-sm text-success-700 dark:text-success-300">
          <i class="fa fa-check-circle mr-2"></i>已选择：{{ selectedFileName }}
        </p>
        
        <!-- 上传错误提示 -->
        <div v-if="knowledgeStore.uploadError" class="mb-6 p-4 bg-gradient-to-r from-error-50 to-red-50 dark:from-error-900/20 dark:to-red-900/20 border border-error-200 dark:border-error-700 rounded-xl">
          <div class="flex items-center text-error-700 dark:text-error-300">
            <i class="fa fa-exclamation-triangle mr-3 text-lg"></i>
            <span class="text-sm font-medium">{{ knowledgeStore.uploadError }}</span>
          </div>
        </div>
        
        <div class="flex justify-end gap-3">
          <button @click="resetUpload" class="btn-secondary">取消</button>
          <button 
            @click="handleUploadFile"
            class="btn-success"
            :disabled="!selectedUploadId || !selectedFile || knowledgeStore.isLoading"
          >
            <i v-if="knowledgeStore.isLoading" class="fa fa-spinner fa-spin mr-2"></i>
            <i v-else class="fa fa-upload mr-2"></i>
            上传
          </button>
        </div>
      </div>
    </div>

    <!-- 语音通话弹窗 -->
    <VoiceCallModal
      :show="showVoiceModal"
      :character-name="characterStore.currentCharacter?.name || 'AI助手'"
      :character-avatar="characterStore.currentCharacter?.avatarUrl || ''"
      :character-id="characterStore.selectedCharacterId || ''"
      :memory-id="chatStore.currentMemoryId || ''"
      :knowledge-id="knowledgeStore.selectedId || ''"
      @close="endVoiceCall"
      @add-message="handleVoiceMessage"
    />

    <!-- 角色管理弹窗 -->
    <div v-if="showCharacterManagement" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="bg-white dark:bg-gray-800 rounded-2xl w-full max-w-6xl max-h-[90vh] overflow-hidden animate-scale-in">
        <div class="p-6 border-b border-gray-200 dark:border-gray-700">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white">角色管理</h2>
            <button
              @click="showCharacterManagement = false"
              class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg transition-colors duration-200"
            >
              <i class="fa fa-times text-xl"></i>
            </button>
          </div>
        </div>
        <div class="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
          <CharacterManagement />
        </div>
      </div>
    </div>

    <!-- 音色管理弹窗 -->
    <div v-if="showVoiceManagement" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="bg-white dark:bg-gray-800 rounded-2xl w-full max-w-6xl max-h-[90vh] overflow-hidden animate-scale-in">
        <div class="p-6 border-b border-gray-200 dark:border-gray-700">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white">音色管理</h2>
            <button
              @click="showVoiceManagement = false"
              class="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg transition-colors duration-200"
            >
              <i class="fa fa-times text-xl"></i>
            </button>
          </div>
        </div>
        <div class="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
          <VoiceManagement />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import { useAuthStore } from '../store/auth'
import { useKnowledgeStore } from '../store/knowledge'
import { useChatStore } from '../store/chat'
import { useCharacterStore } from '../store/character'
import PermissionGuard from '../components/PermissionGuard.vue'
import MessageContent from '../components/MessageContent.vue'
import ButterflyIcon from '../components/ButterflyIcon.vue'
import SidebarToggle from '../components/SidebarToggle.vue'
import CharacterSelector from '../components/CharacterSelector.vue'
import VoiceCallModal from '../components/VoiceCallModal.vue'
import CharacterManagement from '../components/CharacterManagement.vue'
import VoiceManagement from '../components/VoiceManagement.vue'
import { useRouter } from 'vue-router'

// 状态管理实例
const authStore = useAuthStore()
const knowledgeStore = useKnowledgeStore()
const chatStore = useChatStore()
const characterStore = useCharacterStore()
const sidebarToggle = ref(null)
const router = useRouter()

// 计算属性
const isUserInfoLoaded = computed(() => {
  return authStore.userInfo && authStore.userInfo.username
})

// 响应式变量
const darkMode = ref(localStorage.getItem('darkMode') === 'true')
const showCreateModal = ref(false)
const newKnowledgeName = ref('')
const showUploadModal = ref(false)
const selectedUploadId = ref('')
const selectedFile = ref(null)
const selectedFileName = ref('')

// 图片上传相关
const imageInput = ref(null)
const selectedImages = ref([])

// 本地输入框文本
const inputText = ref('')

// 创建知识库相关状态
const createSuccess = ref(false)
const createError = ref('')
const newCreatedKnowledge = ref(null)
const isCreating = ref(false) // 新增：控制创建过程中的加载状态

// 切换标签页 - 默认显示知识库
const activeTab = ref('knowledge')

// 聊天消息列表引用
const chatMessagesContainer = ref(null)

// 语音通话相关状态
const isVoiceCalling = ref(false)

// 音色管理相关状态
const showVoiceManagement = ref(false)
const showVoiceModal = ref(false)

// 角色管理状态
const showCharacterManagement = ref(false)

// 页面初始化
onMounted(async () => {
  // 初始化暗黑模式
  if (darkMode.value) document.documentElement.classList.add('dark')
  
  // 双重检查登录状态：localStorage + store
  const hasToken = !!localStorage.getItem('accessToken')
  const isLogin = authStore.isLogin
  
  if (hasToken && isLogin) {
    // 只有在确认登录的情况下才执行初始化
    try {
      // 先初始化用户信息
      await authStore.initUserInfo()
      
      // 用户信息初始化成功后，再初始化知识库列表和历史会话
      if (authStore.isLogin) {
        await Promise.all([
          knowledgeStore.fetchKnowledgeList(),
          chatStore.fetchChatHistoryList()
        ])
        
        // 初始化聊天，显示欢迎消息
        chatStore.initChat()
      } else {
        // 如果用户信息初始化后登录状态变为false，跳转登录
        router.push('/login')
      }
    } catch (error) {
      console.error('初始化失败:', error)
      // 如果初始化失败，清除token并跳转登录
      authStore.logout()
    }
  } else {
    // 未登录状态，清除可能存在的无效token
    // 路由守卫会自动处理重定向，这里不需要手动跳转
    if (hasToken) {
      authStore.logout()
    }
  }
})

// 解析消息文本函数
const parseMessageText = (message, isQuestion = false) => {
  if (!message) return ''
  
  // 如果是用户问题，需要解析UserMessage格式
  if (isQuestion && typeof message === 'string' && message.includes('UserMessage') && message.includes('contents = [')) {
    return chatStore.parseUserMessageString(message)
  }
  
  // 对于AI回答，直接返回文本，不需要解析JSON
  return message
}

// 时间格式化函数
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  }
  // 小于24小时
  if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  }
  // 小于7天
  if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  }
  // 超过7天显示具体日期
  return date.toLocaleDateString()
}

// 暗黑模式切换
watch(darkMode, (val) => {
  localStorage.setItem('darkMode', val)
  document.documentElement.classList.toggle('dark', val)
})
const toggleDarkMode = () => (darkMode.value = !darkMode.value)

// 创建知识库
const handleCreateKnowledge = async () => {
  if (!newKnowledgeName.value.trim()) {
    createError.value = '知识库名称不能为空'
    return
  }
  isCreating.value = true // 开始创建
  try {
    const knowledge = await knowledgeStore.createKnowledge({ name: newKnowledgeName.value })
    createSuccess.value = true
    newCreatedKnowledge.value = knowledge
    setTimeout(() => {
      closeCreateModal()
    }, 3000) // 3秒后自动关闭
  } catch (err) {
    createError.value = err.message || '创建失败'
  } finally {
    isCreating.value = false // 结束创建
  }
}

// 关闭创建知识库弹窗
const closeCreateModal = () => {
  showCreateModal.value = false
  createSuccess.value = false
  createError.value = ''
  newCreatedKnowledge.value = null
  newKnowledgeName.value = ''
  isCreating.value = false // 重置创建状态
}

// 上传文件：选择文件
const handleFileSelect = (e) => {
  if (e.target.files[0]) {
    selectedFile.value = e.target.files[0]
    selectedFileName.value = e.target.files[0].name
  }
}

// 上传文件：提交
const handleUploadFile = async () => {
  try {
    await knowledgeStore.uploadKnowledgeFile(selectedUploadId.value, selectedFile.value)
    resetUpload()
    alert('上传成功')
  } catch (err) {
    alert(err.message || '上传失败')
  }
}

// 重置上传状态
const resetUpload = () => {
  showUploadModal.value = false
  selectedUploadId.value = ''
  selectedFile.value = null
  selectedFileName.value = ''
  document.querySelector('input[type="file"]').value = ''
}

// 开始语音通话
const startVoiceCall = () => {
  // 检查是否选择了角色
  if (!characterStore.hasSelectedCharacter) {
    alert('请先选择一个角色')
    return
  }
  
  // 检查是否有当前会话ID
  if (!chatStore.currentMemoryId) {
    chatStore.createNewChat()
  }
  
  isVoiceCalling.value = true
  showVoiceModal.value = true
}

// 结束语音通话
const endVoiceCall = () => {
  isVoiceCalling.value = false
  showVoiceModal.value = false
}

// 处理语音消息（从语音弹窗发送过来的消息）
const handleVoiceMessage = (message) => {
  // 将消息添加到聊天界面
  chatStore.messages.push({
    id: String(Date.now() + Math.random()),
    memoryId: chatStore.currentMemoryId,
    type: message.type,
    contents: [{
      type: 'text',
      value: message.content
    }],
    timestamp: message.timestamp
  })
  
  // 滚动到底部
  nextTick(() => {
    if (chatMessagesContainer.value) {
      chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight
    }
  })
}

// 发送聊天消息
const handleSendMessage = () => {
  // 检查是否有文字内容（必须）
  if (!inputText.value.trim()) {
    alert('请输入消息内容')
    return
  }
  
  // 检查是否选择了角色（必须）
  if (!characterStore.hasSelectedCharacter) {
    alert('请先选择一个角色')
    return
  }
  
  // 使用角色聊天
  chatStore.sendCharacterMessage(
    characterStore.selectedCharacterId,
    knowledgeStore.selectedId || null, 
    selectedImages.value, 
    inputText.value
  )
  
  // 发送后清空选中的图片和输入框
  selectedImages.value = []
  inputText.value = ''
}

// 重试失败的消息
const handleRetryMessage = async () => {
  // 检查是否选择了角色
  if (!characterStore.hasSelectedCharacter) {
    alert('请先选择一个角色')
    return
  }
  
  // 先设置输入框内容为失败的消息内容
  if (chatStore.lastFailedMessage) {
    inputText.value = chatStore.lastFailedMessage.content
    // 如果有图片，也恢复图片
    if (chatStore.lastFailedMessage.images && chatStore.lastFailedMessage.images.length > 0) {
      selectedImages.value = [...chatStore.lastFailedMessage.images]
    }
  }
  
  // 调用重试方法
  await chatStore.retryFailedMessage()
  
  // 重试后清空输入框和图片（重试成功后会自动清空）
  if (chatStore.lastFailedMessage === null) {
    inputText.value = ''
    selectedImages.value = []
  }
}

// 新建会话
const handleCreateNewChat = () => {
  chatStore.createNewChat()
}

// 图片上传触发器
const triggerImageUpload = () => {
  // 检查ref是否正确设置
  if (!imageInput.value) {
    console.error('imageInput ref 未正确设置')
    alert('图片上传组件初始化失败，请刷新页面重试')
    return
  }
  
  imageInput.value.click()
}

// 处理图片选择
const handleImageSelect = (e) => {
  const files = Array.from(e.target.files)
  files.forEach(file => {
    if (file) {
      const reader = new FileReader()
      reader.onloadend = () => {
        selectedImages.value = [{
          name: file.name,
          file: file, // 保存文件对象
          url: reader.result,
          preview: reader.result
        }]
      }
      reader.readAsDataURL(file)
    }
  })
  e.target.value = '' // 清空文件输入框的值
}

// 移除选中的图片
const removeImage = (index) => {
  selectedImages.value = []
}

// 清空所有选中的图片
const clearSelectedImages = () => {
  selectedImages.value = []
}

// 图片预览
const previewImage = (image) => {
  // 获取图片URL，优先使用imageUrl，然后是url，最后是preview
  const imageUrl = image.imageUrl || image.url || image.preview
  if (imageUrl) {
    window.open(imageUrl, '_blank')
  }
}

// 处理图片加载错误
const handleImageError = (event) => {
  console.error('图片加载失败:', event.target.src)
  // 替换为占位图
  event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik03NSA3NUM3NSA3NSA3NSA3NSA3NSA3NVoiIGZpbGw9IiM5Q0EzQUYiLz4KPHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIwIDIwQzIwIDIwIDIwIDIwIDIwIDIwWiIgZmlsbD0iIzlDQTNBRiIvPgo8L3N2Zz4KPC9zdmc+'
  event.target.onerror = null // 防止重复触发
}

// 处理知识库选择变化
const handleKnowledgeChange = (event) => {
  const selectedId = event.target.value
  if (selectedId) {
    knowledgeStore.setSelectedId(selectedId)
  } else {
    knowledgeStore.clearSelectedId()
  }
}

// 自动滚动到聊天消息底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesContainer.value) {
      chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动到底部
watch(() => chatStore.messages.length, () => {
  scrollToBottom()
}, { flush: 'post' })
</script>

<style scoped>
/* 动画效果 */
.animate-fadeIn {
  animation: fadeIn 0.3s ease-in-out;
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 自定义主题色（与 Tailwind 配置对应） */
:root {
  --primary-color: #4F46E5; /* 对应 tailwind.config.js 中的 primary */
}

.text-primary { color: var(--primary-color); }
.bg-primary { background-color: var(--primary-color); }
.border-primary { border-color: var(--primary-color); }
.hover\:bg-primary\/90:hover { background-color: rgba(79, 70, 229, 0.9); }
.hover\:bg-primary\/10:hover { background-color: rgba(79, 70, 229, 0.1); }
.focus\:ring-primary:focus { ring-color: var(--primary-color); }

/* 自定义滚动条样式 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.8);
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(55, 65, 81, 0.6);
  border: 1px solid rgba(31, 41, 55, 0.8);
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.8);
  border: 1px solid rgba(55, 65, 81, 0.9);
}

/* 侧边栏内容滚动区域样式 */
.sidebar-content-scroll {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
  -webkit-overflow-scrolling: touch;
}

.sidebar-content-scroll::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content-scroll::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 3px;
}

.sidebar-content-scroll::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.6);
  border-radius: 3px;
  border: 1px solid transparent;
  background-clip: content-box;
}

.sidebar-content-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.8);
  background-clip: content-box;
}

.dark .sidebar-content-scroll::-webkit-scrollbar-thumb {
  background: rgba(55, 65, 81, 0.7);
  border: 1px solid rgba(31, 41, 55, 0.8);
}

.dark .sidebar-content-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.8);
  border: 1px solid rgba(55, 65, 81, 0.9);
}

/* 聊天消息列表滚动区域样式 */
.chat-messages-container {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
  /* 确保在移动端也能正常滚动 */
  -webkit-overflow-scrolling: touch;
  /* 防止内容溢出 */
  contain: layout style paint;
}

.chat-messages-container::-webkit-scrollbar {
  width: 8px;
}

.chat-messages-container::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
}

.chat-messages-container::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.6);
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

.chat-messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.8);
  background-clip: content-box;
}

.dark .chat-messages-container::-webkit-scrollbar-thumb {
  background: rgba(55, 65, 81, 0.7);
  border: 1px solid rgba(31, 41, 55, 0.8);
}

.dark .chat-messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(75, 85, 99, 0.8);
  border: 1px solid rgba(55, 65, 81, 0.9);
}

/* 移动端滚动优化 */
@media (max-width: 768px) {
  .chat-messages-container {
    scrollbar-width: none;
  }
  
  .chat-messages-container::-webkit-scrollbar {
    width: 4px;
  }
}

/* 聊天消息气泡动画 */
@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-messages-container > div {
  animation: messageSlideIn 0.3s ease-out;
}

/* 消息气泡特殊样式 */
.message-bubble {
  position: relative;
}

.message-bubble::before {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  border-style: solid;
}

/* 用户消息气泡尾巴 */
.message-bubble.user::before {
  right: -8px;
  bottom: 0;
  border-width: 0 0 8px 8px;
  border-color: transparent transparent #3b82f6 transparent;
}

/* AI消息气泡尾巴 */
.message-bubble.ai::before {
  left: -8px;
  bottom: 0;
  border-width: 0 8px 8px 0;
  border-color: transparent #ffffff transparent transparent;
}

.dark .message-bubble.ai::before {
  border-color: transparent #374151 transparent transparent;
}
</style>
