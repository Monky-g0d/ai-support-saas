package org.example.controller;

import org.example.dto.MessageDto;
import org.example.model.Message;
import org.example.model.Ticket;
import org.example.model.User;
import org.example.repository.MessageRepository;
import org.example.repository.TicketRepository;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets/{ticketId}/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<MessageDto> create(@PathVariable Long ticketId, @RequestBody MessageDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        Message m = new Message();
        m.setTicket(ticket);
        m.setSender(user);
        m.setBody(dto.getBody());
        messageRepository.save(m);

        dto.setId(m.getId());
        dto.setSenderUsername(user.getUsername());
        dto.setTicketId(ticket.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> list(@PathVariable Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        List<MessageDto> list = messageRepository.findByTicketOrderByCreatedAtAsc(ticket).stream().map(m -> {
            MessageDto dto = new MessageDto();
            dto.setId(m.getId());
            dto.setBody(m.getBody());
            dto.setSenderUsername(m.getSender().getUsername());
            dto.setTicketId(ticket.getId());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
