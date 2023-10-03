package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;
import static com.reifocs.playground.service.RestBean.all;

@Service("SequentialAsync")
public class SequentialAsync implements SeekFunction {
    private final RestBean restBean;

    public SequentialAsync(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        Executor executor = Executors.newCachedThreadPool();

        var futures = LIBRARY_URLS
                .stream()
                .map(libraryUrl -> CompletableFuture.supplyAsync(() -> restBean.seekInLibrary(id, libraryUrl), executor))
                .toList();
        try {
            var results = all(futures).get();
            return results.stream().filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
