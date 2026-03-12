package org.example.backend2.service;

import org.example.backend2.client.Backend1Client;
import org.example.backend2.client.OpenAiClient;
import org.example.backend2.dto.AiResponseDto;
import org.example.backend2.dto.KnowledgeArticleDto;
import org.example.backend2.dto.MessageDto;
import org.example.backend2.dto.TicketDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private Backend1Client backend1Client;

    @Mock
    private OpenAiClient openAiClient;

    @InjectMocks
    private AiService aiService;

    private TicketDto testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new TicketDto();
        testTicket.setId(1L);
        testTicket.setTitle("Не работает оплата");
        testTicket.setDescription("При оплате картой выдаёт ошибку 500");
        testTicket.setStatus("OPEN");
        testTicket.setPriority("HIGH");
    }

    @Test
    void generateResponse_withOpenAi_returnsAiResponse() {
        // Given
        when(backend1Client.getTicket(1L)).thenReturn(testTicket);
        when(backend1Client.getMessages(1L)).thenReturn(Collections.emptyList());
        when(backend1Client.searchKnowledge(anyString())).thenReturn(Collections.emptyList());
        when(openAiClient.isAvailable()).thenReturn(true);
        when(openAiClient.generateResponse(anyString(), anyString())).thenReturn("Здравствуйте! Ошибка 500 может быть связана с...");

        // When
        AiResponseDto response = aiService.generateResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getTicketId());
        assertEquals("Не работает оплата", response.getTicketTitle());
        assertEquals("openai", response.getSource());
        assertNotNull(response.getAiResponse());
    }

    @Test
    void generateResponse_withoutOpenAi_usesKnowledgeBaseFallback() {
        // Given
        KnowledgeArticleDto article = new KnowledgeArticleDto();
        article.setTitle("Инструкция по оплате");
        article.setContent("Для оплаты используйте карты Visa или Mastercard...");
        article.setTags(Set.of("оплата"));

        when(backend1Client.getTicket(1L)).thenReturn(testTicket);
        when(backend1Client.getMessages(1L)).thenReturn(Collections.emptyList());
        when(backend1Client.searchKnowledge(anyString())).thenReturn(List.of(article));
        when(openAiClient.isAvailable()).thenReturn(false);

        // When
        AiResponseDto response = aiService.generateResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals("knowledge_base", response.getSource());
        assertTrue(response.getAiResponse().contains("Инструкция по оплате"));
    }

    @Test
    void generateResponse_ticketNotFound_throwsException() {
        // Given
        when(backend1Client.getTicket(999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> aiService.generateResponse(999L));
    }

    @Test
    void classifyTicket_withoutOpenAi_usesKeywords() {
        // Given
        when(openAiClient.isAvailable()).thenReturn(false);

        // When & Then
        assertEquals("BILLING", aiService.classifyTicket("Проблема с оплатой"));
        assertEquals("BUG_REPORT", aiService.classifyTicket("Ошибка при загрузке"));
        assertEquals("ACCOUNT", aiService.classifyTicket("Не могу зайти в аккаунт"));
        assertEquals("GENERAL", aiService.classifyTicket("Вопрос по продукту"));
    }
}
