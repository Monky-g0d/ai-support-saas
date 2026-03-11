package org.example.controller;

import org.example.dto.TicketDto;
import org.example.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketDto> create(@RequestBody TicketDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        TicketDto created = ticketService.createTicket(dto, userDetails.getUsername());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<TicketDto>> list(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        Page<TicketDto> page = ticketService.listTickets(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> get(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        TicketDto dto = ticketService.getTicket(id, userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }
}
