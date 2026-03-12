package org.example.backend2.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * HTTP-клиент для OpenAI Chat Completions API.
 * Генерирует AI-ответы для тикетов саппорта.
 */
@Component
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    public OpenAiClient(@Qualifier("openaiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Проверяет, доступен ли OpenAI API (наличие ключа).
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Генерация ответа через OpenAI GPT.
     *
     * @param systemPrompt системный промпт (контекст саппорта)
     * @param userMessage  сообщение пользователя (тикет + контекст)
     * @return сгенерированный AI-ответ
     */
    public String generateResponse(String systemPrompt, String userMessage) {
        if (!isAvailable()) {
            log.warn("OpenAI API key not configured, skipping AI generation");
            return null;
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)),
                    "max_tokens", maxTokens,
                    "temperature", temperature);

            JsonNode response = webClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("choices") && response.get("choices").size() > 0) {
                String content = response.get("choices").get(0).get("message").get("content").asText();
                log.info("OpenAI response generated successfully ({} chars)", content.length());
                return content;
            }

            log.warn("Empty response from OpenAI");
            return null;

        } catch (Exception e) {
            log.error("OpenAI API call failed: {}", e.getMessage());
            return null;
        }
    }
}
