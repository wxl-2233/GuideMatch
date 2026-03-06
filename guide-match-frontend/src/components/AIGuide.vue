<template>
  <div class="ai-guide-section">
    <div class="ai-guide-header">
      <div class="ai-icon">🤖</div>
      <h2 class="ai-title">AI智能向导</h2>
      <p class="ai-subtitle">基于智增增AI技术，为您提供个性化推荐</p>
    </div>
    
    <div class="ai-guide-content">
      <div class="chat-container">
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
      
      <div class="ai-features">
        <div class="feature-card">
          <div class="feature-icon">🎯</div>
          <h3>精准匹配</h3>
          <p>根据您的需求智能推荐最适合的向导</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3>快速响应</h3>
          <p>秒级回复，即时解答您的疑问</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔒</div>
          <h3>隐私保护</h3>
          <p>严格保护您的个人信息和对话内容</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
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

onMounted(() => {
  // 获取快速建议
  quickActions.value = aiService.getQuickSuggestions().slice(0, 4).map(text => ({ text }))
  
  // 检查AI服务配置
  const config = aiService.checkConfig()
  if (!config.hasApiKey) {
    console.warn('AI服务未正确配置，请设置API密钥')
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
.ai-guide-section {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 32px;
  margin-top: 32px;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.1);
}

.ai-guide-header {
  text-align: center;
  margin-bottom: 32px;
}

.ai-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.ai-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary);
  margin: 0 0 8px 0;
}

.ai-subtitle {
  font-size: 16px;
  color: var(--text-muted);
  margin: 0;
}

.ai-guide-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 32px;
}

.chat-container {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--border-color);
}

.chat-messages {
  height: 300px;
  overflow-y: auto;
  margin-bottom: 20px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
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

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.quick-action-btn {
  padding: 8px 12px;
  border: 1px solid var(--primary);
  background: white;
  color: var(--primary);
  border-radius: 20px;
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
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

.send-btn {
  padding: 12px 16px;
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

.ai-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.5);
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid var(--border-color);
  transition: transform 0.2s;
}

.feature-card:hover {
  transform: translateY(-2px);
}

.feature-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary);
  margin: 0 0 8px 0;
}

.feature-card p {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .ai-guide-content {
    grid-template-columns: 1fr;
  }
  
  .ai-features {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
  
  .feature-card {
    padding: 16px 12px;
  }
  
  .feature-icon {
    font-size: 24px;
  }
  
  .feature-card h3 {
    font-size: 14px;
  }
  
  .feature-card p {
    font-size: 12px;
  }
}
</style>
