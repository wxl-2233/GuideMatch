<template>
  <div class="ai-guide-container">
    <!-- 左侧聊天区域 -->
    <div class="chat-section">
      <div class="chat-header">
        <div class="ai-info">
          <div class="ai-avatar">🤖</div>
          <div class="ai-details">
            <h3 class="ai-name">AI智能向导</h3>
            <p class="ai-status">在线</p>
          </div>
        </div>
      </div>
      
      <div class="chat-messages" ref="chatMessages">
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          class="message"
          :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
        >
          <div class="message-avatar">
            {{ message.type === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            {{ message.content }}
          </div>
        </div>
      </div>
      
      <div class="chat-input-container">
        <div class="quick-actions">
          <button 
            v-for="action in quickActions" 
            :key="action.text"
            class="quick-action-btn"
            @click="sendQuickAction(action.text)"
          >
            {{ action.text }}
          </button>
        </div>
        
        <div class="chat-input-wrapper">
          <input
            v-model="inputMessage"
            type="text"
            placeholder="输入您的问题..."
            class="chat-input"
            @keypress.enter="sendMessage"
          />
          <button 
            class="send-btn"
            @click="sendMessage"
            :disabled="!inputMessage.trim() || isLoading"
          >
            {{ isLoading ? '⏳' : '📤' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 右侧功能介绍 -->
    <div class="features-section">
      <div class="features-header">
        <h3 class="features-title">功能特色</h3>
        <p class="features-subtitle">智能向导，贴心服务</p>
      </div>
      
      <div class="features-list">
        <div class="feature-item">
          <div class="feature-icon">🎯</div>
          <div class="feature-content">
            <h4>精准匹配</h4>
            <p>根据您的需求智能推荐最适合的向导，节省您的时间</p>
          </div>
        </div>
        
        <div class="feature-item">
          <div class="feature-icon">⚡</div>
          <div class="feature-content">
            <h4>快速响应</h4>
            <p>秒级回复，即时解答您的疑问，提供实时帮助</p>
          </div>
        </div>
        
        <div class="feature-item">
          <div class="feature-icon">🔒</div>
          <div class="feature-content">
            <h4>隐私保护</h4>
            <p>严格保护您的个人信息和对话内容，让您安心使用</p>
          </div>
        </div>
        
        <div class="feature-item">
          <div class="feature-icon">💬</div>
          <div class="feature-content">
            <h4>多语言支持</h4>
            <p>支持中文、英文等多种语言，沟通无障碍</p>
          </div>
        </div>
        
        <div class="feature-item">
          <div class="feature-icon">📊</div>
          <div class="feature-content">
            <h4>数据分析</h4>
            <p>基于大数据分析，为您提供最优向导推荐方案</p>
          </div>
        </div>
      </div>
      
      <div class="status-card">
        <div class="status-indicator" :class="{ 'status-online': isOnline }"></div>
        <div class="status-text">
          <p class="status-title">服务状态</p>
          <p class="status-desc">{{ isOnline ? 'AI服务正常运行' : 'AI服务暂时不可用' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import { useI18n } from '@/composables/useI18n'
import aiService from '@/services/aiService'

const { t } = useI18n()

const messages = ref([
  {
    type: 'ai',
    content: '您好！我是AI智能向导助手，我可以帮您推荐合适的向导，解答关于向导服务的问题。请告诉我您的需求吧！'
  }
])

const inputMessage = ref('')
const isLoading = ref(false)
const chatMessages = ref(null)
const conversationHistory = ref([])
const quickActions = ref([])

// 检查AI服务状态
const isOnline = computed(() => {
  const config = aiService.checkConfig()
  return config.hasApiKey
})

onMounted(() => {
  // 获取快速建议
  quickActions.value = aiService.getQuickSuggestions().slice(0, 4).map(text => ({ text }))
  
  // 检查AI服务配置
  const config = aiService.checkConfig()
  if (!config.hasApiKey) {
    console.warn('AI服务未正确配置，使用降级模式')
    // 添加配置提示消息
    messages.value.push({
      type: 'ai',
      content: '⚠️ AI服务当前使用降级模式，回复可能不够智能。如需完整功能，请联系管理员配置AI服务。'
    })
  }
})

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userMessage
  })
  
  // 添加到对话历史
  conversationHistory.value.push({
    role: 'user',
    content: userMessage
  })
  
  inputMessage.value = ''
  isLoading.value = true
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  try {
    // 调用AI服务
    const response = await aiService.sendMessage(userMessage, conversationHistory.value)
    
    if (response.success) {
      // 添加AI回复
      messages.value.push({
        type: 'ai',
        content: response.content
      })
      
      // 添加到对话历史
      conversationHistory.value.push({
        role: 'assistant',
        content: response.content
      })
      
      // 限制对话历史长度
      if (conversationHistory.value.length > 10) {
        conversationHistory.value = conversationHistory.value.slice(-10)
      }
    } else {
      // 处理错误情况
      messages.value.push({
        type: 'ai',
        content: response.content
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      type: 'ai',
      content: '发送消息时出现错误，请稍后再试。'
    })
  } finally {
    isLoading.value = false
    await nextTick()
    scrollToBottom()
  }
}

const sendQuickAction = (actionText) => {
  inputMessage.value = actionText
  sendMessage()
}

const scrollToBottom = () => {
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-guide-container {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  height: 600px;
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.1);
}

/* 左侧聊天区域 */
.chat-section {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  background: rgba(139, 92, 246, 0.05);
  border-bottom: 1px solid var(--border-color);
}

.ai-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.ai-details h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
}

.ai-details p {
  margin: 0;
  font-size: 12px;
  color: var(--text-muted);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.3);
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: var(--primary);
  color: white;
}

.ai-message .message-avatar {
  background: #f0f0f0;
  color: #333;
}

.message-content {
  flex: 1;
  padding: 12px 16px;
  border-radius: 12px;
  max-width: 80%;
  font-size: 14px;
  line-height: 1.4;
}

.user-message .message-content {
  background: var(--primary);
  color: white;
  margin-left: auto;
}

.ai-message .message-content {
  background: white;
  color: #333;
  border: 1px solid var(--border-color);
}

.chat-input-container {
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.5);
  border-top: 1px solid var(--border-color);
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-action-btn {
  padding: 6px 12px;
  border: 1px solid var(--primary);
  background: white;
  color: var(--primary);
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-action-btn:hover {
  background: var(--primary);
  color: white;
}

.chat-input-wrapper {
  display: flex;
  gap: 8px;
}

.chat-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

.send-btn {
  padding: 10px 14px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: #7c3aed;
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 右侧功能介绍 */
.features-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.features-header {
  text-align: center;
  padding: 20px;
  background: rgba(139, 92, 246, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.features-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--primary);
}

.features-subtitle {
  margin: 0;
  font-size: 14px;
  color: var(--text-muted);
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: transform 0.2s;
}

.feature-item:hover {
  transform: translateY(-2px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.feature-content h4 {
  margin: 0 0 6px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.feature-content p {
  margin: 0;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.4;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ef4444;
  flex-shrink: 0;
}

.status-indicator.status-online {
  background: #10b981;
}

.status-text p {
  margin: 0;
}

.status-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.status-desc {
  font-size: 12px;
  color: var(--text-muted);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .ai-guide-container {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .chat-section {
    height: 500px;
  }
  
  .features-section {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .ai-guide-container {
    padding: 16px;
  }
  
  .features-section {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    display: none;
  }
}
</style>
