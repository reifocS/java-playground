package com.reifocs.playground.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;
import static com.reifocs.playground.service.RestBean.all;

@Service("SequentialAsync")
@Primary
public class SequentialAsync implements SeekFunction {
    private final RestBean restBean;

    public SequentialAsync(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        var futures = LIBRARY_URLS.stream().map(libraryUrl -> restBean.seekInLibraryAsync(id, libraryUrl)).toList();
        try {
            var results = all(futures).get();
            return results.stream().filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
