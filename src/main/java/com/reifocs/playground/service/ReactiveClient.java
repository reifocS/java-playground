package com.reifocs.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ReactiveClient {

    Logger logger = LoggerFactory.getLogger(RestBean.class);

    private final WebClient webClient;

    public ReactiveClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("").build(); // Set your base URL here
    }

    public Mono<String> seekInLibrary(int id, String libraryUrl) {
        String url = libraryUrl + id;
        logger.info(url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class);
    }
}
