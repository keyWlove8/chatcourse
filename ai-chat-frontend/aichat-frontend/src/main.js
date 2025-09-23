import { createApp } from 'vue'
import pinia from './store'
import router from './router'
import App from './App.vue'

// 全局样式
import './assets/css/main.css'

// 创建 App 实例
const app = createApp(App)

// 安装插件
app.use(pinia)
app.use(router)

// 挂载 App
app.mount('#app')