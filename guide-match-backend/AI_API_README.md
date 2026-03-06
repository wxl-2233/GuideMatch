# AI智能向导后端接口文档

## 📋 概述

本文档描述了AI智能向导功能的RESTful API接口，基于智增增AI服务实现。

## 🔗 接口列表

### 1. AI对话接口

**接口地址：** `POST /api/ai/chat`

**功能描述：** 与AI智能向导进行对话

**请求参数：**
```json
{
  "message": "用户输入的消息内容",
  "conversationHistory": [
    {
      "role": "user",
      "content": "用户的历史消息"
    },
    {
      "role": "assistant", 
      "content": "AI的历史回复"
    }
  ]
}
```

**响应示例：**
```json
{
  "success": true,
  "content": "AI的回复内容",
  "usage": {
    "prompt_tokens": 50,
    "completion_tokens": 100,
    "total_tokens": 150
  }
}
```

**错误响应：**
```json
{
  "success": false,
  "content": "降级回复内容",
  "error": "API调用失败的具体原因"
}
```

### 2. 快速建议接口

**接口地址：** `GET /api/ai/quick-suggestions`

**功能描述：** 获取预设的快速提问建议

**响应示例：**
```json
{
  "success": true,
  "suggestions": [
    "推荐北京留学向导",
    "推荐上海旅游向导",
    "推荐计算机考研向导",
    "查看向导收费标准",
    "如何选择合适的向导？",
    "向导服务包含什么？"
  ]
}
```

### 3. AI服务状态检查

**接口地址：** `GET /api/ai/status`

**功能描述：** 检查AI服务的配置状态

**响应示例：**
```json
{
  "success": true,
  "status": "configured",
  "apiUrl": "https://api.zhizengzeng.com/v1",
  "hasApiKey": true
}
```

## ⚙️ 配置说明

### 环境变量配置

在 `.env` 文件或环境变量中配置以下参数：

```bash
# 智增增API密钥（必需）
ZHIZENGZENG_API_KEY=sk-your-actual-api-key-here

# API基础URL（可选，默认为智增增官方地址）
ZHIZENGZENG_API_URL=https://api.zhizengzeng.com/v1

# AI模型（可选，默认gpt-3.5-turbo）
ZHIZENGZENG_MODEL=gpt-3.5-turbo

# 最大令牌数（可选，默认500）
ZHIZENGZENG_MAX_TOKENS=500

# 温度参数（可选，默认0.7）
ZHIZENGZENG_TEMPERATURE=0.7
```

### application.yml配置

```yaml
ai:
  zhizengzeng:
    api:
      key: ${ZHIZENGZENG_API_KEY:your-api-key-here}
      url: ${ZHIZENGZENG_API_URL:https://api.zhizengzeng.com/v1}
      model: ${ZHIZENGZENG_MODEL:gpt-3.5-turbo}
      max-tokens: ${ZHIZENGZENG_MAX_TOKENS:500}
      temperature: ${ZHIZENGZENG_TEMPERATURE:0.7}
```

## 🛠️ 技术实现

### 架构设计

```
Frontend (Vue.js)
    ↓ HTTP请求
AIController (REST API)
    ↓ 调用服务
AIService (业务逻辑)
    ↓ HTTP调用
智增增API (外部服务)
```

### 核心类说明

#### AIController.java
- **职责：** REST API控制器
- **功能：** 处理HTTP请求，参数验证，响应封装
- **接口：** `/api/ai/*`

#### AIService.java
- **职责：** AI服务业务逻辑
- **功能：** API调用，错误处理，降级响应
- **依赖：** RestTemplate, ObjectMapper

### 错误处理机制

1. **API调用失败：** 自动降级到预设回复
2. **网络异常：** 返回友好的错误提示
3. **配置缺失：** 返回未配置状态
4. **参数错误：** 返回400错误

### 降级策略

当智增增API不可用时，系统会根据用户输入的关键词返回相关的预设回复：

- **推荐类问题：** 引导用户使用筛选功能
- **价格类问题：** 引导查看详情页面
- **其他问题：** 通用降级回复

## 🔒 安全考虑

### API密钥安全
- 使用环境变量存储API密钥
- 不在代码中硬编码敏感信息
- 支持不同环境的配置

### 请求限制
- 建议在前端实现请求频率限制
- 可在后端添加拦截器进行限流

### 内容过滤
- 通过系统提示词限制AI回复内容范围
- 避免生成不当或敏感内容

## 📊 监控建议

### 关键指标
- API调用成功率
- 响应时间
- 降级触发频率
- 用户满意度

### 日志记录
- 记录API调用日志
- 记录错误和降级情况
- 监控API使用量

## 🚀 部署说明

### 开发环境
```bash
# 设置环境变量
export ZHIZENGZENG_API_KEY=your-api-key

# 启动应用
mvn spring-boot:run
```

### 生产环境
```bash
# 使用环境变量文件
docker run -e ZHIZENGZENG_API_KEY=your-api-key your-app

# 或使用Kubernetes配置
kubectl apply -f deployment.yaml
```

## 📝 使用示例

### JavaScript/TypeScript调用示例

```javascript
// AI对话
const response = await fetch('/api/ai/chat', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    message: '推荐一个北京留学向导',
    conversationHistory: []
  })
});

const result = await response.json();
console.log(result.content);

// 获取快速建议
const suggestions = await fetch('/api/ai/quick-suggestions');
const suggestionsData = await suggestions.json();
console.log(suggestionsData.suggestions);
```

### Java调用示例

```java
// 使用RestTemplate调用
RestTemplate restTemplate = new RestTemplate();

Map<String, Object> request = Map.of(
    "message", "推荐一个北京留学向导",
    "conversationHistory", List.of()
);

ResponseEntity<Map> response = restTemplate.postForEntity(
    "/api/ai/chat", 
    request, 
    Map.class
);

Map<String, Object> result = response.getBody();
System.out.println(result.get("content"));
```

## 🐛 故障排除

### 常见问题

1. **API密钥未配置**
   - 检查环境变量 `ZHIZENGZENG_API_KEY`
   - 调用 `/api/ai/status` 确认配置状态

2. **API调用超时**
   - 检查网络连接
   - 确认API地址配置正确

3. **回复内容异常**
   - 检查系统提示词配置
   - 确认模型参数设置

### 调试建议

1. 查看 `/api/ai/status` 接口返回的配置状态
2. 检查应用日志中的API调用记录
3. 使用Postman等工具直接测试接口

## 📞 技术支持

如需技术支持，请联系：
- 开发团队：[邮箱地址]
- 智增增客服：[客服联系方式]

---

**注意：** 请确保在生产环境中正确配置API密钥，并遵守智增增的服务条款。
