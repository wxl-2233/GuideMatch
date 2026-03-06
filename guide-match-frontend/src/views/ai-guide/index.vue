<template>
  <div class="ai-guide-page">
    <PageContainer>
      <div class="ai-guide-container">
        <!-- 页面标题 -->
        <div class="ai-guide-header">
          <h1 class="page-title">AI智能向导助手</h1>
          <p class="page-subtitle">基于智增增AI技术，为您提供个性化向导推荐和智能问答服务</p>
        </div>

        <!-- AI对话区域 -->
        <div class="ai-chat-section">
          <div class="chat-container">
            <div class="chat-header">
              <div class="ai-info">
                <div class="ai-avatar-large">🤖</div>
                <div class="ai-details">
                  <h3>AI智能向导</h3>
                  <span class="status-indicator">在线</span>
                </div>
              </div>
              <button class="clear-chat-btn" @click="clearChat">
                🗑️ 清空对话
              </button>
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
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                </div>
              </div>
              
              <!-- 加载指示器 -->
              <div v-if="isLoading" class="message ai-message">
                <div class="message-avatar">🤖</div>
                <div class="message-content">
                  <div class="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="chat-input-container">
              <div class="quick-actions">
                <div class="quick-actions-title">快速提问：</div>
                <div class="quick-actions-grid">
                  <button 
                    v-for="action in quickActions" 
                    :key="action.text"
                    class="quick-action-btn"
                    @click="sendQuickAction(action.text)"
                  >
                    {{ action.text }}
                  </button>
                </div>
              </div>
              
              <div class="chat-input-wrapper">
                <textarea
                  v-model="inputMessage"
                  placeholder="输入您的问题，AI助手将为您提供专业建议..."
                  class="chat-input"
                  @keypress.enter.prevent="handleEnter"
                  rows="3"
                ></textarea>
                <button 
                  class="send-btn"
                  @click="sendMessage"
                  :disabled="!inputMessage.trim() || isLoading"
                >
                  <span v-if="isLoading">⏳</span>
                  <span v-else>📤</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 功能介绍 -->
        <div class="ai-features-section">
          <h2 class="features-title">AI助手功能</h2>
          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">🎯</div>
              <h3>精准推荐</h3>
              <p>根据您的具体需求，智能推荐最合适的向导，包括专业领域、价格范围、服务类型等</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">⚡</div>
              <h3>实时问答</h3>
              <p>秒级响应您的咨询，解答关于向导服务、流程安排、注意事项等各种问题</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">🔍</div>
              <h3>深度分析</h3>
              <p>分析向导的专业背景、用户评价、服务经验，为您提供全方位的参考信息</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">💡</div>
              <h3>个性化建议</h3>
              <p>根据您的偏好和预算，提供个性化的向导选择建议和优化方案</p>
            </div>
          </div>
        </div>

        <!-- 使用统计 -->
        <div class="ai-stats-section">
          <h2 class="stats-title">服务统计</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalUsers }}</div>
              <div class="stat-label">服务用户</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ stats.totalGuides }}</div>
              <div class="stat-label">推荐向导</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ stats.successRate }}%</div>
              <div class="stat-label">满意度</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ stats.avgResponse }}s</div>
              <div class="stat-label">响应时间</div>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import PageContainer from '@/components/layout/PageContainer.vue'
import aiService from '@/services/aiService'

const messages = ref([
  {
    type: 'ai',
    content: '您好！我是AI智能向导助手，很高兴为您服务！我可以帮您：\n\n🎯 推荐合适的向导\n📋 解答向导服务相关问题\n💡 提供个性化建议\n🔍 分析向导信息\n\n请告诉我您的需求吧！',
    timestamp: new Date()
  }
])

const inputMessage = ref('')
const isLoading = ref(false)
const chatMessages = ref(null)
const conversationHistory = ref([])

const quickActions = ref([])

const stats = ref({
  totalUsers: 1286,
  totalGuides: 342,
  successRate: 96,
  avgResponse: 2.3
})

onMounted(() => {
  // 获取快速建议
  quickActions.value = aiService.getQuickSuggestions().map(text => ({ text }))
  
  // 检查AI服务配置
  const config = aiService.checkConfig()
  if (!config.hasApiKey) {
    console.warn('AI服务未正确配置，请设置API密钥')
  }
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
})

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userMessage,
    timestamp: new Date()
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
        content: response.content,
        timestamp: new Date()
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
        content: response.content,
        timestamp: new Date()
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      type: 'ai',
      content: '发送消息时出现错误，请稍后再试。',
      timestamp: new Date()
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

const clearChat = () => {
  messages.value = [
    {
      type: 'ai',
      content: '您好！我是AI智能向导助手，很高兴为您服务！我可以帮您：\n\n🎯 推荐合适的向导\n📋 解答向导服务相关问题\n💡 提供个性化建议\n🔍 分析向导信息\n\n请告诉我您的需求吧！',
      timestamp: new Date()
    }
  ]
  conversationHistory.value = []
  nextTick(() => {
    scrollToBottom()
  })
}

const handleEnter = (event) => {
  if (event.shiftKey) {
    // Shift+Enter 换行
    return
  } else {
    // Enter 发送
    sendMessage()
  }
}

const scrollToBottom = () => {
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}
</script>

<style scoped>
.ai-guide-page {
  min-height: 100vh;
  background: var(--bg-main);
}

.ai-guide-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.ai-guide-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  color: var(--primary);
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 18px;
  color: var(--text-muted);
  margin: 0;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.ai-chat-section {
  margin-bottom: 64px;
}

.chat-container {
  background: var(--card-bg);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(139, 92, 246, 0.1);
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.chat-header {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ai-avatar-large {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.ai-details h3 {
  color: white;
  margin: 0 0 4px 0;
  font-size: 18px;
}

.status-indicator {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  position: relative;
  padding-left: 16px;
}

.status-indicator::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(16, 185, 129, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}

.clear-chat-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.clear-chat-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 24px;
  background: rgba(255, 255, 255, 0.5);
}

.message {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  animation: fadeInUp 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
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
  max-width: 80%;
}

.user-message .message-content {
  margin-left: auto;
}

.message-text {
  padding: 16px 20px;
  border-radius: 16px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.user-message .message-text {
  background: var(--primary);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-text {
  background: white;
  color: #333;
  border: 1px solid var(--border-color);
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 8px;
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 16px 20px;
  background: white;
  border: 1px solid var(--border-color);
  border-radius: 16px;
  border-bottom-left-radius: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #666;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.chat-input-container {
  padding: 24px;
  background: rgba(255, 255, 255, 0.3);
  border-top: 1px solid var(--border-color);
}

.quick-actions {
  margin-bottom: 20px;
}

.quick-actions-title {
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.quick-action-btn {
  padding: 12px 16px;
  border: 1px solid var(--primary);
  background: white;
  color: var(--primary);
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.quick-action-btn:hover {
  background: var(--primary);
  color: white;
  transform: translateY(-2px);
}

.chat-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 16px;
  background: white;
  resize: none;
  font-family: inherit;
  line-height: 1.5;
}

.chat-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.send-btn {
  padding: 16px 20px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 20px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 60px;
}

.send-btn:hover:not(:disabled) {
  background: #7c3aed;
  transform: translateY(-2px);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ai-features-section {
  margin-bottom: 64px;
}

.features-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary);
  text-align: center;
  margin-bottom: 32px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  background: var(--card-bg);
  padding: 32px;
  border-radius: 16px;
  text-align: center;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary);
  margin: 0 0 16px 0;
}

.feature-card p {
  font-size: 16px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.6;
}

.ai-stats-section {
  margin-bottom: 64px;
}

.stats-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary);
  text-align: center;
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.stat-card {
  background: var(--card-bg);
  padding: 32px;
  border-radius: 16px;
  text-align: center;
  border: 1px solid var(--border-color);
}

.stat-number {
  font-size: 48px;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .ai-guide-container {
    padding: 0 16px;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .page-subtitle {
    font-size: 16px;
  }
  
  .chat-messages {
    height: 300px;
    padding: 16px;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chat-input-wrapper {
    flex-direction: column;
    gap: 12px;
  }
  
  .send-btn {
    width: 100%;
  }
}
</style>
