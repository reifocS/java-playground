package com.reifocs.playground.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service
public class RestBean {
    public Optional<String> seekInLibrary(int id, String libraryUrl) {
        String url = libraryUrl + id;
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
    CompletableFuture<Optional<String>> seekInLibraryAsync(int id, String libraryUrl) {
        return CompletableFuture.completedFuture(seekInLibrary(id, libraryUrl));
    }

    @Async
    void statefulAsync(int id, CountDownLatch doneSignal, AtomicReference<Optional<String>> result, String libraryUrl) {
        var data = seekInLibrary(id, libraryUrl);
        doneSignal.countDown();
        if (data.isPresent()) {
            result.set(data);
            for (int i = 0; i < LIBRARY_URLS.size(); i++) {
                doneSignal.countDown();
            }
        }
    }

    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }
}
