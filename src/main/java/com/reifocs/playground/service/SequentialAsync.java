package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Objects;
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
        return LIBRARY_URLS
                .stream()
                .map(libraryUrl -> CompletableFuture.supplyAsync(() -> restBean.seekInLibrary(id, libraryUrl), Runnable::run))
                .map(this::fullFillPromise)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .findAny()
                .orElse(Optional.empty());
    }
}
