package com.reifocs.playground.controller;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    public static final List<String> LIBRARY_URLS = IntStream.range(0, 10)
            .mapToObj(i -> "http://localhost:%d/books/".formatted(3000 + i))
            .toList();
    Logger logger = LoggerFactory.getLogger(PortalController.class);

    @GetMapping("/seek/{id}")
    public String seekBook(@PathVariable int id) {
        // Define a list of library server URLs
        // Iterate over the library server URLs and perform GET requests
        Optional<String> dest = seekAsyncMultiTasks(id);
        // Book not found in any library
        assert dest.isPresent();
        return dest.get();
    }

    private Optional<String> seekParallelSync(int id) {
        return LIBRARY_URLS.parallelStream().map(libraryUrl -> seekInLibrary(id, libraryUrl))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.of("Book not found"));
    }

    private Optional<String> seekSequentialSync(int id) {
        return LIBRARY_URLS.stream().map(libraryUrl -> seekInLibrary(id, libraryUrl))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.of("Book not found"));
    }

    private Optional<String> seekParallelAsync(int id) {
        return LIBRARY_URLS.parallelStream().map(libraryUrl -> seekInLibraryAsync(id, libraryUrl))
                .map(optionalFuture -> {
                    try {
                        return optionalFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.of("Book not found"));
    }


    public Optional<String> seekAsyncMultiTasks(int id) {
        final CountDownLatch doneSignal = new CountDownLatch(LIBRARY_URLS.size());
        final AtomicReference<Optional<String>> result = new AtomicReference<>();
        for (String libraryUrl : LIBRARY_URLS) {
            CompletableFuture.supplyAsync(() -> {
                logger.info("%s%d".formatted(libraryUrl, id));
                var data = seekInLibrary(id, libraryUrl);
                doneSignal.countDown();
                if (data.isPresent()) {
                    result.set(data);
                    for (int i = 0; i < LIBRARY_URLS.size(); i++) {
                        doneSignal.countDown();
                    }
                }
                return data;
            });
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }


    private static Optional<String> seekInLibrary(int id, String libraryUrl) {
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
    public Future<Optional<String>> seekInLibraryAsync(int id, String libraryUrl) {
        return CompletableFuture.completedFuture(seekInLibrary(id, libraryUrl));

    }
}
