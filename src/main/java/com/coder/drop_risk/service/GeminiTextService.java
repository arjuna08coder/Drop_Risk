package com.coder.drop_risk.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiTextService {
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiTextService(
            RestClient.Builder restClientBuilder,
            @Value("${google.ai.gemini.api-key:}") String apiKey,
            @Value("${google.ai.gemini.model:gemini-3.5-flash-lite}") String model) {
        this.restClient = restClientBuilder.baseUrl("https://generativelanguage.googleapis.com/v1beta").build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generate(String systemInstruction, List<Turn> turns) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is not configured.");
        }
        List<Map<String, Object>> contents = new ArrayList<>();
        for (Turn turn : turns) {
            if (turn == null || turn.text() == null || turn.text().isBlank()) continue;
            Map<String, Object> content = new LinkedHashMap<>();
            content.put("role", "model".equals(turn.role()) ? "model" : "user");
            content.put("parts", List.of(Map.of("text", turn.text())));
            contents.add(content);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
        body.put("contents", contents);
        body.put("generationConfig", Map.of("temperature", 0.7, "topP", 0.9, "maxOutputTokens", 256));

        JsonNode response = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        JsonNode parts = response == null ? null : response.at("/candidates/0/content/parts");
        if (parts == null || !parts.isArray()) {
            throw new IllegalStateException("Gemini returned no text content.");
        }
        StringBuilder text = new StringBuilder();
        for (JsonNode part : parts) {
            JsonNode value = part.get("text");
            if (value != null && value.isTextual() && !value.asText().isBlank()) {
                if (!text.isEmpty()) text.append('\n');
                text.append(value.asText().trim());
            }
        }
        if (text.isEmpty()) throw new IllegalStateException("Gemini returned an empty response.");
        return text.toString();
    }

    public record Turn(String role, String text) {}
}
