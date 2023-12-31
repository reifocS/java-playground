package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("ParallelAsyncAll")
public class ParallelAsyncAll implements SeekFunction {

    private final RestBean restBean;
    public ParallelAsyncAll(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {

        return LIBRARY_URLS
                .parallelStream()
                .map(libraryUrl -> CompletableFuture.supplyAsync(() -> restBean.seekInLibrary(id, libraryUrl)))
                .map(this::fullFillPromise)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .findAny()
                .orElse(Optional.empty());
    }
}
