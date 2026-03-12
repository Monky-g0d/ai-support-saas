package org.example.backend2.dto;

import java.util.Set;

public class KnowledgeArticleDto {
    private Long id;
    private String title;
    private String content;
    private Set<String> tags;

    public KnowledgeArticleDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
