package org.example.backend2.controller;

import org.example.backend2.dto.TicketStatsDto;
import org.example.backend2.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API для аналитики и дашборда.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Статистика тикетов: всего, по статусам, по приоритетам.
     * GET /api/analytics/tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<TicketStatsDto> getTicketStats() {
        log.info("GET /api/analytics/tickets");
        try {
            TicketStatsDto stats = analyticsService.getTicketStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting ticket stats: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Общий обзор системы (health-check + статистика).
     * GET /api/analytics/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        log.info("GET /api/analytics/overview");
        Map<String, Object> overview = analyticsService.getOverview();
        return ResponseEntity.ok(overview);
    }
}
