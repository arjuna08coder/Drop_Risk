package com.coder.drop_risk.controller;

import com.coder.drop_risk.dto.ChatRequest;
import com.coder.drop_risk.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/chat")
@CrossOrigin(origins = "*")
public class AiChatController {
    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);
    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) { this.aiChatService = aiChatService; }

    @PostMapping
    public ResponseEntity<Map<String, String>> chatWithAi(@RequestBody ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Enter a message to chat with the assistant."));
        }
        if (request.getMessage().length() > 2000) {
            return ResponseEntity.badRequest().body(Map.of("error", "Messages must be 2,000 characters or fewer."));
        }
        try {
            String response = aiChatService.chat(request.getMessage(), request.getStudentId(), request.getHistory());
            return ResponseEntity.ok(Map.of("response", response == null ? "The assistant returned an empty response." : response));
        } catch (Exception exception) {
            log.error("AI chat request failed", exception);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error", "The assistant could not complete that request. Check the server configuration and retry."));
        }
    }
}