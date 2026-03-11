package org.example.service;

import org.example.dto.TicketDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {
    TicketDto createTicket(TicketDto dto, String username);
    Page<TicketDto> listTickets(String username, Pageable pageable);
    TicketDto getTicket(Long id, String username);
}
