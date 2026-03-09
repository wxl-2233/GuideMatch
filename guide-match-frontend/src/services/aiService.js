// AI服务配置
const AI_CONFIG = {
  // 智增增API配置
  zhizengzeng: {
    baseURL: 'https://api.zhizengzeng.com/v1',
    apiKey: process.env.VUE_APP_ZHIZENGZENG_API_KEY || 'your-api-key-here',
    model: 'gpt-3.5-turbo',
    maxTokens: 500,
    temperature: 0.7
  },
  
  // 系统提示词
  systemPrompt: `你是GuideMatch平台的AI智能向导助手。你的职责是：
1. 根据用户的需求推荐合适的向导
2. 解答关于向导服务和平台使用的问题
3. 提供旅游、留学、商务等向导服务建议
4. 解答向导注册、认证、费用等平台相关问题
5. 保持友好、专业的语气

请根据用户的具体问题提供详细、有用的回答。如果用户询问向导推荐，请询问他们的具体需求，如：
- 目的地城市
- 服务类型（旅游、留学、商务、本地向导等）
- 预算范围
- 时间安排
- 特殊要求等

如果用户询问平台相关问题，请详细解释GuideMatch的：
- 向导注册流程
- 认证标准和要求
- 服务费用结构
- 平台保障措施
- 用户权益保护等`
}

// AI服务类
class AIService {
  constructor() {
    this.config = AI_CONFIG.zhizengzeng
  }

  // 发送消息到AI
  async sendMessage(userMessage, conversationHistory = []) {
    try {
      const messages = [
        {
          role: 'system',
          content: this.config.systemPrompt
        },
        ...conversationHistory,
        {
          role: 'user',
          content: userMessage
        }
      ]

      const response = await fetch(`${this.config.baseURL}/chat/completions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.config.apiKey}`
        },
        body: JSON.stringify({
          model: this.config.model,
          messages: messages,
          max_tokens: this.config.maxTokens,
          temperature: this.config.temperature,
          stream: false
        })
      })

      if (!response.ok) {
        throw new Error(`API请求失败: ${response.status} ${response.statusText}`)
      }

      const data = await response.json()
      return {
        success: true,
        content: data.choices[0].message.content,
        usage: data.usage
      }
    } catch (error) {
      console.error('AI服务调用失败:', error)
      return {
        success: false,
        error: error.message,
        content: this.getFallbackResponse(userMessage)
      }
    }
  }

  // 降级处理：返回预设回复
  getFallbackResponse(userMessage) {
    const fallbackResponses = [
      '抱歉，AI服务暂时不可用。您可以尝试联系客服或浏览我们的向导列表寻找合适的向导。',
      '网络连接出现问题，请稍后再试。您可以先查看我们的向导列表。',
      'AI助手正在维护中，建议您直接浏览向导列表或联系在线客服。'
    ]

    // 根据关键词返回相关回复
    if (userMessage.includes('推荐') || userMessage.includes('找')) {
      return '抱歉，AI推荐服务暂时不可用。建议您使用上方的筛选功能来找到合适的向导，或者联系我们的客服人员获得帮助。'
    }
    
    if (userMessage.includes('价格') || userMessage.includes('收费')) {
      return '关于价格信息，建议您直接查看向导详情页面，上面有明确的价格标注。如需更多帮助，请联系客服。'
    }

    return fallbackResponses[Math.floor(Math.random() * fallbackResponses.length)]
  }

  // 获取快速回复建议
  getQuickSuggestions() {
    return [
      '推荐北京旅游向导',
      '推荐上海留学向导', 
      '推荐深圳商务向导',
      '如何注册成为向导？',
      '向导认证流程是什么？',
      '平台服务费用如何计算？'
    ]
  }

  // 检查API配置
  checkConfig() {
    return {
      hasApiKey: !!this.config.apiKey && this.config.apiKey !== 'your-api-key-here',
      baseURL: this.config.baseURL,
      model: this.config.model
    }
  }
}

// 创建单例
const aiService = new AIService()

export default aiService
