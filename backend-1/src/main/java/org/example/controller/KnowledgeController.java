package org.example.controller;

import org.example.dto.KnowledgeArticleDto;
import org.example.model.KnowledgeArticle;
import org.example.model.User;
import org.example.repository.KnowledgeArticleRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeArticleRepository repo;
    private final UserRepository userRepository;

    public KnowledgeController(KnowledgeArticleRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<KnowledgeArticleDto>> list(Pageable pageable) {
        Page<KnowledgeArticle> page = repo.findAll(pageable);
        Page<KnowledgeArticleDto> dtoPage = new PageImpl<>(page.stream().map(this::toDto).collect(Collectors.toList()), pageable, page.getTotalElements());
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<KnowledgeArticleDto>> search(@RequestParam String q, Pageable pageable) {
        Page<KnowledgeArticle> page = repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);
        Page<KnowledgeArticleDto> dtoPage = new PageImpl<>(page.stream().map(this::toDto).collect(Collectors.toList()), pageable, page.getTotalElements());
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity<KnowledgeArticleDto> create(@RequestBody KnowledgeArticleDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        KnowledgeArticle a = new KnowledgeArticle();
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setTags(dto.getTags());
        a.setCreatedBy(user);
        repo.save(a);
        dto.setId(a.getId());
        return ResponseEntity.ok(dto);
    }

    private KnowledgeArticleDto toDto(KnowledgeArticle a) {
        KnowledgeArticleDto dto = new KnowledgeArticleDto();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setTags(a.getTags());
        return dto;
    }
}
