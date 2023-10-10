package com.reifocs.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class RestBean {

    Logger logger = LoggerFactory.getLogger(RestBean.class);


    public Optional<String> seekInLibrary(int id, String libraryUrl) {
        String url = libraryUrl + id;
        logger.info(url);
        Optional<String> empty = Optional.empty();
        try {
            ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Book found in the current library server
                return Optional.of("Book found in Library %s%d".formatted(libraryUrl, id));
            }
        } catch (HttpClientErrorException notFound) {
            System.out.println("Book not found in Library");
            return empty;
        }
        return empty;
    }

    @Async
    public CompletableFuture<Optional<String>> seekInLibraryAsync(int id, String libraryUrl) {
        String url = libraryUrl + id;
        logger.info(url);
        Optional<String> empty = Optional.empty();
        try {
            ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Book found in the current library server
                return CompletableFuture.completedFuture(Optional.of("Book found in Library %s%d".formatted(libraryUrl, id)));
            }
        } catch (HttpClientErrorException notFound) {
            System.out.println("Book not found in Library");
            return CompletableFuture.completedFuture(empty);
        }
        return CompletableFuture.completedFuture(empty);
    }

    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }
}
