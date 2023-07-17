package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;
import static com.reifocs.playground.service.RestBean.all;

@Service("ParallelAsync")
public class ParallelAsync implements SeekFunction {

    private final RestBean restBean;

    public ParallelAsync(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        var futures = LIBRARY_URLS.parallelStream()
                .map(libraryUrl -> restBean.seekInLibraryAsync(id, libraryUrl))
                .toList();
        try {
            var results = all(futures).get();
            return results.stream().filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T fullFillPromise(Future<T> prom) {
        try {
            return prom.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception promesse - " + e.getMessage());
            return null;
        }
    }

}
