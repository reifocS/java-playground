package com.reifocs.playground.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

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
        var futures = LIBRARY_URLS
                .stream()
                .map(libraryUrl -> restBean.seekInLibraryAsync(id, libraryUrl))
                .toList();
        var results = all(futures).join();
        return results.stream().filter(Optional::isPresent).findAny().orElse(Optional.empty());
    }
}
