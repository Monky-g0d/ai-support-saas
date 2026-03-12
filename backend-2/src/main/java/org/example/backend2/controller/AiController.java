package org.example.backend2.controller;

import org.example.backend2.dto.AiResponseDto;
import org.example.backend2.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API для AI-функциональности.
 * Генерация ответов на тикеты, классификация, авто-ответы.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * Генерирует AI-ответ для тикета (без автоматической отправки).
     * POST /api/ai/respond/{ticketId}
     */
    @PostMapping("/respond/{ticketId}")
    public ResponseEntity<AiResponseDto> generateResponse(@PathVariable Long ticketId) {
        log.info("POST /api/ai/respond/{}", ticketId);
        try {
            AiResponseDto response = aiService.generateResponse(ticketId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating AI response for ticket #{}: {}", ticketId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Генерирует AI-ответ И автоматически отправляет его в тикет через Backend #1.
     * POST /api/ai/auto-respond/{ticketId}
     */
    @PostMapping("/auto-respond/{ticketId}")
    public ResponseEntity<AiResponseDto> autoRespond(@PathVariable Long ticketId) {
        log.info("POST /api/ai/auto-respond/{}", ticketId);
        try {
            AiResponseDto response = aiService.autoRespond(ticketId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error auto-responding to ticket #{}: {}", ticketId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Классифицирует описание тикета по категориям.
     * POST /api/ai/classify
     * Body: { "description": "..." }
     */
    @PostMapping("/classify")
    public ResponseEntity<Map<String, String>> classifyTicket(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        if (description == null || description.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "description is required"));
        }

        log.info("POST /api/ai/classify");
        String category = aiService.classifyTicket(description);
        return ResponseEntity.ok(Map.of("category", category));
    }
}
