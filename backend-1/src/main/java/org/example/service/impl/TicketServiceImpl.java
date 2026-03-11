package org.example.service.impl;

import org.example.dto.TicketDto;
import org.example.model.Ticket;
import org.example.model.User;
import org.example.repository.TicketRepository;
import org.example.repository.UserRepository;
import org.example.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketDto createTicket(TicketDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setCreatedBy(user);
        ticketRepository.save(ticket);
        dto.setId(ticket.getId());
        return dto;
    }

    @Override
    public Page<TicketDto> listTickets(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Page<Ticket> page = ticketRepository.findByCreatedBy(user, pageable);
        List<TicketDto> dtos = page.stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public TicketDto getTicket(Long id, String username) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toDto(ticket);
    }

    private TicketDto toDto(Ticket t) {
        TicketDto dto = new TicketDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        return dto;
    }
}
