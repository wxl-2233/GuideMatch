package com.guide.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class AIService {

    @Value("${ai.zhizengzeng.api.key:your-api-key-here}")
    private String zhizengzengApiKey;

    @Value("${ai.zhizengzeng.api.url:https://api.zhizengzeng.com/v1}")
    private String zhizengzengApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 系统提示词
    private static final String SYSTEM_PROMPT = "你是一个专业的向导推荐助手，名为\"AI向导助手\"。你的职责是：\n" +
            "1. 根据用户的需求推荐合适的向导\n" +
            "2. 解答关于向导服务的问题\n" +
            "3. 提供旅行、留学、考研等相关建议\n" +
            "4. 保持友好、专业的语气\n" +
            "\n" +
            "请根据用户的具体问题提供详细、有用的回答。如果用户询问向导推荐，请询问他们的具体需求，如：\n" +
            "- 目的地城市\n" +
            "- 服务类型（旅游、留学、考研等）\n" +
            "- 预算范围\n" +
            "- 时间安排\n" +
            "- 特殊要求等";

    public Map<String, Object> chat(String userMessage, List<Map<String, Object>> conversationHistory) {
        try {
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);

            // 添加对话历史
            if (conversationHistory != null) {
                for (Map<String, Object> msg : conversationHistory) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", (String) msg.get("role"));
                    message.put("content", (String) msg.get("content"));
                    messages.add(message);
                }
            }

            // 添加用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // 调用智增增API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(zhizengzengApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        zhizengzengApiUrl + "/chat/completions",
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode responseBody = objectMapper.readTree(response.getBody());
                    String content = responseBody.path("choices").get(0).path("message").path("content").asText();
                    JsonNode usage = responseBody.path("usage");

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("content", content);
                    result.put("usage", Map.of(
                            "prompt_tokens", usage.path("prompt_tokens").asInt(),
                            "completion_tokens", usage.path("completion_tokens").asInt(),
                            "total_tokens", usage.path("total_tokens").asInt()
                    ));

                    return result;
                } else {
                    return Map.of("success", false, "error", "API调用失败");
                }
            } catch (Exception e) {
                // API调用失败，返回降级响应
                return Map.of(
                        "success", false,
                        "content", getFallbackResponse(userMessage),
                        "error", e.getMessage()
                );
            }

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "请求处理失败: " + e.getMessage()
            );
        }
    }

    public List<String> getQuickSuggestions() {
        return List.of(
                "推荐北京留学向导",
                "推荐上海旅游向导",
                "推荐计算机考研向导",
                "查看向导收费标准",
                "如何选择合适的向导？",
                "向导服务包含什么？"
        );
    }

    public Map<String, Object> checkStatus() {
        boolean hasApiKey = !zhizengzengApiKey.equals("your-api-key-here") && !zhizengzengApiKey.isEmpty();

        return Map.of(
                "success", true,
                "status", hasApiKey ? "configured" : "not_configured",
                "apiUrl", zhizengzengApiUrl,
                "hasApiKey", hasApiKey
        );
    }

    private String getFallbackResponse(String userMessage) {
        List<String> fallbackResponses = List.of(
                "抱歉，AI服务暂时不可用。您可以尝试联系客服或浏览我们的向导列表寻找合适的向导。",
                "网络连接出现问题，请稍后再试。您可以先查看我们的向导列表。",
                "AI助手正在维护中，建议您直接浏览向导列表或联系在线客服。"
        );

        // 根据关键词返回相关回复
        if (userMessage.contains("推荐") || userMessage.contains("找")) {
            return "抱歉，AI推荐服务暂时不可用。建议您使用上方的筛选功能来找到合适的向导，或者联系我们的客服人员获得帮助。";
        }
        
        if (userMessage.contains("价格") || userMessage.contains("收费")) {
            return "关于价格信息，建议您直接查看向导详情页面，上面有明确的价格标注。如需更多帮助，请联系客服。";
        }

        return fallbackResponses.get((int) (Math.random() * fallbackResponses.size()));
    }
}
