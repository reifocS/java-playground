package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("ParallelSync")
public class ParallelSync implements SeekFunction {

    private final RestBean restBean;

    public ParallelSync(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        return LIBRARY_URLS.parallelStream().map(libraryUrl -> restBean.seekInLibrary(id, libraryUrl))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.of("Book not found"));
    }
}
