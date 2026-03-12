package org.example.backend2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${backend1.url}")
    private String backend1Url;

    @Value("${openai.api.url}")
    private String openaiUrl;

    @Bean("backend1WebClient")
    public WebClient backend1WebClient() {
        return WebClient.builder()
                .baseUrl(backend1Url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean("openaiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(openaiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
