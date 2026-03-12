package org.example.backend2.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.backend2.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP-клиент для взаимодействия с Backend #1 REST API.
 * Использует JWT-авторизацию для защищенных endpoints.
 */
@Component
public class Backend1Client {

    private static final Logger log = LoggerFactory.getLogger(Backend1Client.class);

    private final WebClient webClient;

    @Value("${backend1.username}")
    private String username;

    @Value("${backend1.password}")
    private String password;

    private String cachedToken;

    public Backend1Client(@Qualifier("backend1WebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Авторизация в Backend #1 и получение JWT-токена.
     */
    public String login() {
        try {
            Map<String, String> body = Map.of(
                    "username", username,
                    "password", password);

            AuthResponse response = webClient.post()
                    .uri("/api/auth/login")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(AuthResponse.class)
                    .block();

            if (response != null && response.getAccessToken() != null) {
                this.cachedToken = response.getAccessToken();
                log.info("Successfully authenticated with Backend #1");
                return cachedToken;
            }
        } catch (WebClientResponseException e) {
            log.warn("Login failed ({}). Trying to register...", e.getStatusCode());
            return register();
        } catch (Exception e) {
            log.error("Failed to connect to Backend #1: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Регистрация сервисного аккаунта AI-агента в Backend #1.
     */
    private String register() {
        try {
            Map<String, String> body = Map.of(
                    "username", username,
                    "email", username + "@ai-support.local",
                    "password", password);

            AuthResponse response = webClient.post()
                    .uri("/api/auth/register")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(AuthResponse.class)
                    .block();

            if (response != null && response.getAccessToken() != null) {
                this.cachedToken = response.getAccessToken();
                log.info("Registered and authenticated with Backend #1");
                return cachedToken;
            }
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Получает JWT-токен (логин если нет кэшированного).
     */
    private String getToken() {
        if (cachedToken == null) {
            login();
        }
        return cachedToken;
    }

    /**
     * Получить конкретный тикет по ID.
     */
    public TicketDto getTicket(Long ticketId) {
        try {
            return webClient.get()
                    .uri("/api/tickets/{id}", ticketId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(TicketDto.class)
                    .block();
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("Token expired, re-authenticating...");
            cachedToken = null;
            login();
            return webClient.get()
                    .uri("/api/tickets/{id}", ticketId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(TicketDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to get ticket {}: {}", ticketId, e.getMessage());
            return null;
        }
    }

    /**
     * Получить список тикетов (первая страница).
     */
    public List<TicketDto> getTickets() {
        try {
            JsonNode response = webClient.get()
                    .uri("/api/tickets?size=100")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("content")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<TicketDto> tickets = new java.util.ArrayList<>();
                for (JsonNode node : response.get("content")) {
                    tickets.add(mapper.treeToValue(node, TicketDto.class));
                }
                return tickets;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to get tickets: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Получить сообщения тикета.
     */
    public List<MessageDto> getMessages(Long ticketId) {
        try {
            return webClient.get()
                    .uri("/api/tickets/{ticketId}/messages", ticketId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<MessageDto>>() {
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to get messages for ticket {}: {}", ticketId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Отправить сообщение в тикет (AI-ответ).
     */
    public MessageDto sendMessage(Long ticketId, String body) {
        try {
            Map<String, String> requestBody = Map.of("body", body);

            return webClient.post()
                    .uri("/api/tickets/{ticketId}/messages", ticketId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(MessageDto.class)
                    .block();
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("Token expired, re-authenticating...");
            cachedToken = null;
            login();
            Map<String, String> requestBody = Map.of("body", body);
            return webClient.post()
                    .uri("/api/tickets/{ticketId}/messages", ticketId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(MessageDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to send message to ticket {}: {}", ticketId, e.getMessage());
            return null;
        }
    }

    /**
     * Поиск статей в базе знаний.
     */
    public List<KnowledgeArticleDto> searchKnowledge(String query) {
        try {
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/knowledge/search")
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("content")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<KnowledgeArticleDto> articles = new java.util.ArrayList<>();
                for (JsonNode node : response.get("content")) {
                    articles.add(mapper.treeToValue(node, KnowledgeArticleDto.class));
                }
                return articles;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to search knowledge base: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Получить все статьи базы знаний.
     */
    public List<KnowledgeArticleDto> getAllKnowledge() {
        try {
            JsonNode response = webClient.get()
                    .uri("/api/knowledge?size=100")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("content")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<KnowledgeArticleDto> articles = new java.util.ArrayList<>();
                for (JsonNode node : response.get("content")) {
                    articles.add(mapper.treeToValue(node, KnowledgeArticleDto.class));
                }
                return articles;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to get all knowledge articles: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
