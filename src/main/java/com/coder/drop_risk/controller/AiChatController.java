package com.coder.drop_risk.controller;

import com.coder.drop_risk.dto.ChatRequest;
import com.coder.drop_risk.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chatWithAi(@RequestBody ChatRequest request) {
        String aiResponse = aiChatService.chat(request.getMessage(), request.getStudentId());
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}