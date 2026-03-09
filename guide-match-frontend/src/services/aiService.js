// AI服务配置
const AI_CONFIG = {
  // 智增增API配置
  zhizengzeng: {
    baseURL: 'https://api.zhizengzeng.com/v1',
    apiKey: import.meta.env.VITE_ZHIZENGZENG_API_KEY || 'demo-key',
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
    this.isDemoMode = !this.checkConfig().hasApiKey
  }

  // 发送消息到AI
  async sendMessage(userMessage, conversationHistory = []) {
    // 如果是演示模式，直接返回预设回复
    if (this.isDemoMode) {
      return {
        success: true,
        content: this.getDemoResponse(userMessage),
        usage: null
      }
    }

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

  // 演示模式回复
  getDemoResponse(userMessage) {
    const responses = {
      '推荐': '根据您的需求，我推荐您查看我们的向导列表。您可以按照城市、服务类型、价格等条件筛选合适的向导。如果您有具体要求，可以告诉我，我会给您更精准的建议！',
      '找': '我来帮您找到合适的向导！建议您先确定具体需求：目的地是哪里？需要什么类型的向导服务？预算大概是多少？这样我就能给您更精准的推荐了。',
      '价格': '关于向导服务价格，每个向导的收费标准不同。一般来说，日薪在200-800元之间，时薪在50-200元之间。具体价格请查看向导详情页面，上面有明确的报价。',
      '注册': '注册成为向导很简单！1. 点击注册按钮 2. 填写基本信息 3. 完成实名认证 4. 提交相关资质证明 5. 等待审核通过。整个过程一般需要1-3个工作日。',
      '认证': '向导认证需要：1. 身份证实名认证 2. 相关资质证书（如导游证）3. 服务能力证明 4. 用户评价记录。认证通过后会有特殊标识，更容易获得用户信任。',
      '留学': '留学向导可以提供：1. 学校申请指导 2. 签证办理协助 3. 住宿安排建议 4. 生活适应指导 5. 学业规划建议。建议选择有留学经验的向导。'
    }

    // 根据关键词匹配回复
    for (const [keyword, response] of Object.entries(responses)) {
      if (userMessage.includes(keyword)) {
        return response
      }
    }

    // 默认回复
    return '感谢您的咨询！我是AI智能向导助手，可以帮您推荐合适的向导、解答平台使用问题。请告诉我您的具体需求，我会尽力为您提供帮助！'
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
      hasApiKey: !!this.config.apiKey && this.config.apiKey !== 'demo-key' && this.config.apiKey !== 'your-api-key-here',
      baseURL: this.config.baseURL,
      model: this.config.model,
      isDemoMode: this.isDemoMode
    }
  }
}

// 创建单例
const aiService = new AIService()

export default aiService
