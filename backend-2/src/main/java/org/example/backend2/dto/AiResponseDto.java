package org.example.backend2.dto;

public class AiResponseDto {
    private Long ticketId;
    private String ticketTitle;
    private String aiResponse;
    private boolean sentToBackend;
    private String source; // "openai" или "knowledge_base"

    public AiResponseDto() {}

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getTicketTitle() { return ticketTitle; }
    public void setTicketTitle(String ticketTitle) { this.ticketTitle = ticketTitle; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public boolean isSentToBackend() { return sentToBackend; }
    public void setSentToBackend(boolean sentToBackend) { this.sentToBackend = sentToBackend; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
