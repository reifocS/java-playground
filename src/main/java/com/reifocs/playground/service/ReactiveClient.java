package com.reifocs.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReactiveClient {
    private final WebClient webClient;
    Logger logger = LoggerFactory.getLogger(ReactiveClient.class);

    public ReactiveClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("").build();
    }

    public Mono<String> seekInLibrary(int id, String libraryUrl) {
        String url = libraryUrl + id;
        logger.info(url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class,
                ex -> Mono.empty()).map(value -> value.isEmpty() ? value : url);
    }
}
