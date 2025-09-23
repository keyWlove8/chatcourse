<template>
  <div class="butterfly-icon" :class="sizeClass">
    <!-- 左翅膀 -->
    <div class="wing left-wing"></div>
    
    <!-- 右翅膀 -->
    <div class="wing right-wing"></div>
    
    <!-- 身体 -->
    <div class="body">
      <div class="body-dots">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
    </div>
    
    <!-- 触角 -->
    <div class="antenna left-antenna">
      <div class="antenna-tip"></div>
    </div>
    <div class="antenna right-antenna">
      <div class="antenna-tip"></div>
    </div>
    
    <!-- 眼睛 -->
    <div class="eye left-eye">
      <div class="eye-sparkle"></div>
    </div>
    <div class="eye right-eye">
      <div class="eye-sparkle"></div>
    </div>
    
    <!-- 嘴巴 -->
    <div class="mouth"></div>
  </div>
</template>

<script>
export default {
  name: 'ButterflyIcon',
  props: {
    size: {
      type: String,
      default: 'medium', // small, medium, large
      validator: value => ['small', 'medium', 'large'].includes(value)
    }
  },
  computed: {
    sizeClass() {
      return `size-${this.size}`
    }
  }
}
</script>

<style scoped>
.butterfly-icon {
  position: relative;
  display: inline-block;
}

/* 翅膀基础样式 */
.wing {
  position: absolute;
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 50%, #FF8C00 100%);
  border: 2px solid #FF8C00;
  box-shadow: 0 4px 12px rgba(255, 140, 0, 0.3);
}

/* 左翅膀 */
.left-wing {
  left: 0;
  top: 0;
  transform: rotate(-25deg);
}

/* 右翅膀 */
.right-wing {
  right: 0;
  top: 0;
  transform: rotate(25deg);
}

/* 身体 */
.body {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 22px;
  background: linear-gradient(to bottom, #8B4513 0%, #654321 100%);
  border-radius: 3px;
  border: 1px solid #654321;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

/* 身体装饰点 */
.body-dots {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.dot {
  width: 2px;
  height: 2px;
  background: #FFD700;
  border-radius: 50%;
  box-shadow: 0 1px 2px rgba(255, 215, 0, 0.5);
}

/* 眼睛 */
.eye {
  position: absolute;
  top: 50%;
  width: 4px;
  height: 4px;
  background: #000;
  border-radius: 50%;
  border: 1px solid #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

.left-eye {
  left: 50%;
  transform: translate(-50%, -50%) translateX(-2px);
}

.right-eye {
  left: 50%;
  transform: translate(-50%, -50%) translateX(2px);
}

/* 眼睛高光 */
.eye-sparkle {
  position: absolute;
  top: 1px;
  left: 1px;
  width: 1px;
  height: 1px;
  background: #FFF;
  border-radius: 50%;
}

/* 嘴巴 */
.mouth {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%) translateY(8px);
  width: 2px;
  height: 1px;
  background: #8B4513;
  border-radius: 1px;
}

/* 触角 */
.antenna {
  position: absolute;
  top: 0;
  width: 1px;
  height: 8px;
  background: linear-gradient(to bottom, #8B4513 0%, #654321 100%);
  border-radius: 0.5px;
}

.left-antenna {
  left: 50%;
  transform: translateX(-50%) rotate(-40deg);
  transform-origin: bottom center;
}

.right-antenna {
  left: 50%;
  transform: translateX(-50%) rotate(40deg);
  transform-origin: bottom center;
}

.antenna-tip {
  position: absolute;
  top: -3px;
  left: 50%;
  transform: translateX(-50%);
  width: 5px;
  height: 5px;
  background: #FFD700;
  border-radius: 50%;
  border: 1px solid #FF8C00;
  box-shadow: 0 2px 4px rgba(255, 215, 0, 0.5);
}

/* 尺寸变体 */
.size-small {
  width: 24px;
  height: 24px;
}

.size-small .wing {
  width: 12px;
  height: 16px;
}

.size-small .left-wing {
  left: 2px;
  top: 2px;
}

.size-small .right-wing {
  right: 2px;
  top: 2px;
}

.size-small .body {
  width: 4px;
  height: 16px;
}

.size-small .dot {
  width: 1px;
  height: 1px;
}

.size-small .antenna {
  height: 6px;
}

.size-small .antenna-tip {
  width: 3px;
  height: 3px;
}

.size-small .eye {
  width: 2px;
  height: 2px;
}

.size-small .eye-sparkle {
  width: 0.5px;
  height: 0.5px;
}

.size-small .mouth {
  width: 1px;
  height: 0.5px;
  transform: translate(-50%, -50%) translateY(6px);
}

.size-medium {
  width: 32px;
  height: 32px;
}

.size-medium .wing {
  width: 16px;
  height: 20px;
}

.size-medium .left-wing {
  left: 2px;
  top: 2px;
}

.size-medium .right-wing {
  right: 2px;
  top: 2px;
}

.size-medium .body {
  width: 5px;
  height: 18px;
}

.size-medium .antenna {
  height: 7px;
}

.size-medium .antenna-tip {
  width: 4px;
  height: 4px;
}

.size-medium .eye {
  width: 3px;
  height: 3px;
}

.size-medium .eye-sparkle {
  width: 1px;
  height: 1px;
}

.size-medium .mouth {
  transform: translate(-50%, -50%) translateY(7px);
}

.size-large {
  width: 48px;
  height: 48px;
}

.size-large .wing {
  width: 24px;
  height: 30px;
}

.size-large .left-wing {
  left: 4px;
  top: 4px;
}

.size-large .right-wing {
  right: 4px;
  top: 4px;
}

.size-large .body {
  width: 6px;
  height: 22px;
}

.size-large .antenna {
  height: 10px;
}

.size-large .antenna-tip {
  width: 5px;
  height: 5px;
}

.size-large .eye {
  width: 4px;
  height: 4px;
}

.size-large .eye-sparkle {
  width: 1px;
  height: 1px;
}

.size-large .mouth {
  transform: translate(-50%, -50%) translateY(8px);
}

/* 悬停效果 */
.butterfly-icon:hover .wing {
  transform: scale(1.1);
  transition: transform 0.3s ease;
  box-shadow: 0 6px 16px rgba(255, 140, 0, 0.4);
}

.butterfly-icon:hover .left-wing {
  transform: rotate(-25deg) scale(1.1);
}

.butterfly-icon:hover .right-wing {
  transform: rotate(25deg) scale(1.1);
}

.butterfly-icon:hover .antenna-tip {
  box-shadow: 0 3px 8px rgba(255, 215, 0, 0.7);
}

.butterfly-icon:hover .eye {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.7);
}

/* 动画效果 */
@keyframes flutter {
  0%, 100% { transform: rotate(-25deg) scale(1); }
  50% { transform: rotate(-30deg) scale(1.05); }
}

@keyframes flutterRight {
  0%, 100% { transform: rotate(25deg) scale(1); }
  50% { transform: rotate(30deg) scale(1.05); }
}

.butterfly-icon.animate .left-wing {
  animation: flutter 2s ease-in-out infinite;
}

.butterfly-icon.animate .right-wing {
  animation: flutterRight 2s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .size-large {
    width: 40px;
    height: 40px;
  }
  
  .size-large .wing {
    width: 20px;
    height: 25px;
  }
}
</style>
