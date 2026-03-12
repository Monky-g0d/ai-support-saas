package org.example.backend2.service;

import org.example.backend2.client.Backend1Client;
import org.example.backend2.client.OpenAiClient;
import org.example.backend2.dto.AiResponseDto;
import org.example.backend2.dto.KnowledgeArticleDto;
import org.example.backend2.dto.MessageDto;
import org.example.backend2.dto.TicketDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI-сервис для генерации ответов на тикеты.
 * Использует OpenAI GPT + Knowledge Base для формирования контекстных ответов.
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private static final String SYSTEM_PROMPT = """
            Ты — AI-ассистент службы поддержки клиентов. Твоя задача — помогать клиентам,
            отвечая на их вопросы вежливо и профессионально.

            Правила:
            1. Отвечай на языке клиента (русский или английский)
            2. Будь конкретен и полезен
            3. Если есть релевантные статьи из базы знаний — используй их
            4. Если не знаешь ответа — честно скажи об этом и предложи обратиться к живому агенту
            5. Ответ должен быть кратким, но содержательным (не более 3-4 предложений)
            """;

    private final Backend1Client backend1Client;
    private final OpenAiClient openAiClient;

    public AiService(Backend1Client backend1Client, OpenAiClient openAiClient) {
        this.backend1Client = backend1Client;
        this.openAiClient = openAiClient;
    }

    /**
     * Генерирует AI-ответ для тикета.
     * 1. Получает тикет и его сообщения из Backend #1
     * 2. Ищет релевантные статьи в Knowledge Base
     * 3. Формирует промпт и генерирует ответ через OpenAI
     * 4. Если OpenAI недоступен — делает fallback на Knowledge Base
     */
    public AiResponseDto generateResponse(Long ticketId) {
        log.info("Generating AI response for ticket #{}", ticketId);

        // 1. Получаем тикет
        TicketDto ticket = backend1Client.getTicket(ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket #" + ticketId + " not found in Backend #1");
        }

        // 2. Получаем историю сообщений
        List<MessageDto> messages = backend1Client.getMessages(ticketId);

        // 3. Ищем релевантные статьи в базе знаний
        String searchQuery = ticket.getTitle() + " " + ticket.getDescription();
        List<KnowledgeArticleDto> articles = backend1Client.searchKnowledge(searchQuery);

        // 4. Формируем контекст
        String userContext = buildUserContext(ticket, messages, articles);

        // 5. Генерируем ответ
        AiResponseDto response = new AiResponseDto();
        response.setTicketId(ticketId);
        response.setTicketTitle(ticket.getTitle());

        if (openAiClient.isAvailable()) {
            // Используем OpenAI GPT
            String aiAnswer = openAiClient.generateResponse(SYSTEM_PROMPT, userContext);
            if (aiAnswer != null) {
                response.setAiResponse(aiAnswer);
                response.setSource("openai");
                log.info("AI response generated via OpenAI for ticket #{}", ticketId);
                return response;
            }
        }

        // Fallback: используем Knowledge Base
        String fallbackAnswer = generateKnowledgeBaseFallback(ticket, articles);
        response.setAiResponse(fallbackAnswer);
        response.setSource("knowledge_base");
        log.info("AI response generated via Knowledge Base fallback for ticket #{}", ticketId);
        return response;
    }

    /**
     * Генерирует AI-ответ и автоматически отправляет его в тикет через Backend #1.
     */
    public AiResponseDto autoRespond(Long ticketId) {
        AiResponseDto response = generateResponse(ticketId);

        // Отправляем ответ в Backend #1
        String messageBody = "[AI-ответ] " + response.getAiResponse();
        MessageDto sent = backend1Client.sendMessage(ticketId, messageBody);

        response.setSentToBackend(sent != null);
        if (sent != null) {
            log.info("AI response automatically sent to ticket #{}", ticketId);
        } else {
            log.warn("Failed to send AI response to ticket #{}", ticketId);
        }

        return response;
    }

    /**
     * AI-классификация тикета по описанию.
     */
    public String classifyTicket(String description) {
        if (!openAiClient.isAvailable()) {
            return classifyByKeywords(description);
        }

        String classifyPrompt = """
                Ты — классификатор тикетов поддержки. Определи категорию тикета.
                Возможные категории: BILLING, TECHNICAL, GENERAL, ACCOUNT, BUG_REPORT, FEATURE_REQUEST
                Ответь ТОЛЬКО одним словом — названием категории.
                """;

        String result = openAiClient.generateResponse(classifyPrompt, "Описание тикета: " + description);
        return result != null ? result.trim().toUpperCase() : classifyByKeywords(description);
    }

    /**
     * Формирует контекст для AI из тикета, сообщений и статей.
     */
    private String buildUserContext(TicketDto ticket, List<MessageDto> messages, List<KnowledgeArticleDto> articles) {
        StringBuilder context = new StringBuilder();
        context.append("=== ТИКЕТ ===\n");
        context.append("Заголовок: ").append(ticket.getTitle()).append("\n");
        context.append("Описание: ").append(ticket.getDescription()).append("\n");
        context.append("Приоритет: ").append(ticket.getPriority()).append("\n");
        context.append("Статус: ").append(ticket.getStatus()).append("\n\n");

        if (messages != null && !messages.isEmpty()) {
            context.append("=== ИСТОРИЯ СООБЩЕНИЙ ===\n");
            for (MessageDto msg : messages) {
                context.append(msg.getSenderUsername()).append(": ").append(msg.getBody()).append("\n");
            }
            context.append("\n");
        }

        if (articles != null && !articles.isEmpty()) {
            context.append("=== РЕЛЕВАНТНЫЕ СТАТЬИ ИЗ БАЗЫ ЗНАНИЙ ===\n");
            for (KnowledgeArticleDto article : articles) {
                context.append("Статья: ").append(article.getTitle()).append("\n");
                context.append(article.getContent()).append("\n\n");
            }
        }

        context.append("\nСгенерируй полезный ответ для клиента на основе этого контекста.");
        return context.toString();
    }

    /**
     * Fallback: генерация ответа на основе Knowledge Base без AI.
     */
    private String generateKnowledgeBaseFallback(TicketDto ticket, List<KnowledgeArticleDto> articles) {
        if (articles == null || articles.isEmpty()) {
            return "Спасибо за обращение! Ваш тикет \"" + ticket.getTitle()
                    + "\" принят в обработку. Наш специалист свяжется с вами в ближайшее время.";
        }

        StringBuilder response = new StringBuilder();
        response.append("Здравствуйте! По вашему вопросу \"").append(ticket.getTitle()).append("\" ");
        response.append("мы нашли следующую информацию из базы знаний:\n\n");

        for (int i = 0; i < Math.min(articles.size(), 2); i++) {
            KnowledgeArticleDto article = articles.get(i);
            response.append("📄 **").append(article.getTitle()).append("**\n");
            String content = article.getContent();
            if (content.length() > 200) {
                content = content.substring(0, 200) + "...";
            }
            response.append(content).append("\n\n");
        }

        response.append("Если этот ответ не помог, наш специалист свяжется с вами.");
        return response.toString();
    }

    /**
     * Fallback-классификация по ключевым словам (без AI)
     */
    private String classifyByKeywords(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("оплат") || lower.contains("счет") || lower.contains("billing") || lower.contains("pay")) {
            return "BILLING";
        }
        if (lower.contains("ошибк") || lower.contains("error") || lower.contains("bug") || lower.contains("не работ")) {
            return "BUG_REPORT";
        }
        if (lower.contains("аккаунт") || lower.contains("пароль") || lower.contains("логин")
                || lower.contains("account")) {
            return "ACCOUNT";
        }
        if (lower.contains("функци") || lower.contains("добавить") || lower.contains("feature")
                || lower.contains("request")) {
            return "FEATURE_REQUEST";
        }
        if (lower.contains("настрой") || lower.contains("установ") || lower.contains("technical")
                || lower.contains("config")) {
            return "TECHNICAL";
        }
        return "GENERAL";
    }
}
