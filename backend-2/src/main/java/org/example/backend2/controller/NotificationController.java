package org.example.backend2.controller;

import org.example.backend2.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API для управления email-нотификациями.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Отправить уведомление о создании тикета.
     * POST /api/notifications/ticket-created/{ticketId}
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/ticket-created/{ticketId}")
    public ResponseEntity<Map<String, Object>> sendTicketCreatedNotification(
            @PathVariable Long ticketId,
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        log.info("POST /api/notifications/ticket-created/{}", ticketId);
        boolean sent = notificationService.sendTicketCreatedEmail(ticketId, email);
        return ResponseEntity.ok(Map.of("sent", sent, "ticketId", ticketId));
    }

    /**
     * Отправить тестовый email.
     * POST /api/notifications/test
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestNotification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        log.info("POST /api/notifications/test");
        boolean sent = notificationService.sendTestEmail(email);
        return ResponseEntity.ok(Map.of("sent", sent, "email", email));
    }
}
