package org.example.backend2.service;

import org.example.backend2.client.Backend1Client;
import org.example.backend2.dto.TicketDto;
import org.example.backend2.dto.TicketStatsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис аналитики — собирает статистику по тикетам из Backend #1.
 * Предоставляет данные для дашборда админ-панели.
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final Backend1Client backend1Client;

    public AnalyticsService(Backend1Client backend1Client) {
        this.backend1Client = backend1Client;
    }

    /**
     * Статистика тикетов: всего, по статусам, по приоритетам.
     */
    public TicketStatsDto getTicketStats() {
        log.info("Calculating ticket statistics...");

        List<TicketDto> tickets = backend1Client.getTickets();

        TicketStatsDto stats = new TicketStatsDto();
        stats.setTotalTickets(tickets.size());

        // Группировка по статусу
        Map<String, Long> byStatus = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus() != null ? t.getStatus() : "UNKNOWN",
                        Collectors.counting()));
        stats.setByStatus(byStatus);

        // Группировка по приоритету
        Map<String, Long> byPriority = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority() != null ? t.getPriority() : "UNKNOWN",
                        Collectors.counting()));
        stats.setByPriority(byPriority);

        // Счётчики
        stats.setOpenTickets(byStatus.getOrDefault("OPEN", 0L));
        stats.setClosedTickets(byStatus.getOrDefault("CLOSED", 0L));

        log.info("Ticket stats: total={}, open={}, closed={}",
                stats.getTotalTickets(), stats.getOpenTickets(), stats.getClosedTickets());
        return stats;
    }

    /**
     * Общий обзор системы.
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        try {
            TicketStatsDto ticketStats = getTicketStats();
            overview.put("tickets", ticketStats);
            overview.put("status", "online");
            overview.put("backend1Connected", true);
        } catch (Exception e) {
            log.error("Failed to get overview: {}", e.getMessage());
            overview.put("status", "degraded");
            overview.put("backend1Connected", false);
            overview.put("error", e.getMessage());
        }

        overview.put("service", "Backend #2 — AI & Integration");
        overview.put("version", "1.0-SNAPSHOT");
        return overview;
    }
}
