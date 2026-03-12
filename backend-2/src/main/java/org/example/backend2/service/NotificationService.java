package org.example.backend2.service;

import org.example.backend2.client.Backend1Client;
import org.example.backend2.dto.TicketDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Сервис email-нотификаций.
 * Отправляет уведомления при создании тикетов и AI-ответах.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final Backend1Client backend1Client;

    @Value("${notification.from}")
    private String fromEmail;

    @Value("${notification.enabled}")
    private boolean enabled;

    public NotificationService(JavaMailSender mailSender, Backend1Client backend1Client) {
        this.mailSender = mailSender;
        this.backend1Client = backend1Client;
    }

    /**
     * Отправить уведомление о создании нового тикета.
     */
    public boolean sendTicketCreatedEmail(Long ticketId, String recipientEmail) {
        if (!enabled) {
            log.info("Notifications disabled, skipping email for ticket #{}", ticketId);
            return false;
        }

        try {
            TicketDto ticket = backend1Client.getTicket(ticketId);
            if (ticket == null) {
                log.warn("Ticket #{} not found, cannot send notification", ticketId);
                return false;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("🎫 Новый тикет #" + ticketId + ": " + ticket.getTitle());
            message.setText(buildTicketCreatedBody(ticket));

            mailSender.send(message);
            log.info("Ticket created notification sent to {} for ticket #{}", recipientEmail, ticketId);
            return true;

        } catch (Exception e) {
            log.error("Failed to send ticket created email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Отправить уведомление с AI-ответом.
     */
    public boolean sendAiResponseEmail(Long ticketId, String aiResponse, String recipientEmail) {
        if (!enabled) {
            log.info("Notifications disabled, skipping AI response email for ticket #{}", ticketId);
            return false;
        }

        try {
            TicketDto ticket = backend1Client.getTicket(ticketId);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("🤖 AI ответ на тикет #" + ticketId);
            message.setText(buildAiResponseBody(ticket, aiResponse));

            mailSender.send(message);
            log.info("AI response notification sent to {} for ticket #{}", recipientEmail, ticketId);
            return true;

        } catch (Exception e) {
            log.error("Failed to send AI response email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Тестовая отправка email.
     */
    public boolean sendTestEmail(String recipientEmail) {
        if (!enabled) {
            log.info("Notifications disabled, skipping test email");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("🧪 Test — AI Support SaaS Backend #2");
            message.setText(
                    "Это тестовое сообщение от AI Support SaaS Backend #2.\nEmail-нотификации работают корректно!");

            mailSender.send(message);
            log.info("Test email sent to {}", recipientEmail);
            return true;

        } catch (Exception e) {
            log.error("Failed to send test email: {}", e.getMessage());
            return false;
        }
    }

    private String buildTicketCreatedBody(TicketDto ticket) {
        return """
                Создан новый тикет в системе поддержки:

                📋 Тикет #%d
                📝 Заголовок: %s
                📄 Описание: %s
                ⚡ Приоритет: %s
                📊 Статус: %s

                ---
                AI Support SaaS — Автоматическое уведомление
                """.formatted(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus());
    }

    private String buildAiResponseBody(TicketDto ticket, String aiResponse) {
        String ticketTitle = ticket != null ? ticket.getTitle() : "Неизвестный тикет";
        return """
                AI сгенерировал ответ на тикет:

                📋 Тикет: %s

                🤖 AI Ответ:
                %s

                ---
                AI Support SaaS — Автоматическое уведомление
                """.formatted(ticketTitle, aiResponse);
    }
}
