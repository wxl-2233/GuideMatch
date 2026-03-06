<template>
  <div class="ai-floating-button" @click="goToAIGuide">
    <div class="ai-avatar">
      <span class="ai-icon">🤖</span>
    </div>
    <div class="ai-tooltip">
      <span class="tooltip-text">AI智能助手</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToAIGuide = () => {
  router.push('/ai-guide')
}
</script>

<style scoped>
.ai-floating-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  cursor: pointer;
  z-index: 9999;
  transition: all 0.3s ease;
}

.ai-avatar {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.4);
  transition: all 0.3s ease;
  position: relative;
}

.ai-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(139, 92, 246, 0.6);
}

.ai-avatar:active {
  transform: scale(0.95);
}

.ai-icon {
  font-size: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-5px);
  }
  60% {
    transform: translateY(-3px);
  }
}

.ai-tooltip {
  position: absolute;
  bottom: 70px;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  transform: translateY(10px);
}

.ai-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  right: 15px;
  border: 5px solid transparent;
  border-top-color: rgba(0, 0, 0, 0.8);
}

.ai-floating-button:hover .ai-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

/* 脉冲动画效果 */
.ai-avatar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  opacity: 0.3;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.2);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 0.3;
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .ai-floating-button {
    bottom: 20px;
    right: 20px;
    width: 50px;
    height: 50px;
  }
  
  .ai-icon {
    font-size: 20px;
  }
  
  .ai-tooltip {
    bottom: 60px;
    font-size: 12px;
    padding: 6px 10px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .ai-tooltip {
    background: rgba(255, 255, 255, 0.9);
    color: #333;
  }
  
  .ai-tooltip::after {
    border-top-color: rgba(255, 255, 255, 0.9);
  }
}
</style>
