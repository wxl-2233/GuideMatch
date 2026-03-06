package com.guide.controller;

import com.guide.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> conversationHistory = (List<Map<String, Object>>) request.get("conversationHistory");

            Map<String, Object> result = aiService.chat(userMessage, conversationHistory);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.ok(result); // 即使失败也返回200，让前端处理
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "请求处理失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/quick-suggestions")
    public ResponseEntity<?> getQuickSuggestions() {
        List<String> suggestions = aiService.getQuickSuggestions();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "suggestions", suggestions
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkStatus() {
        Map<String, Object> status = aiService.checkStatus();
        return ResponseEntity.ok(status);
    }
}
