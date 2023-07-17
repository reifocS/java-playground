package com.reifocs.playground.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("ParallelAsync")
@Primary
public class ParallelAsync implements SeekFunction {

    private final RestBean restBean;

    public ParallelAsync(RestBean restBean) {
        this.restBean = restBean;
    }

    public static <T> CompletableFuture<T> anyMatch(
            List<? extends CompletionStage<? extends T>> l, Predicate<? super T> criteria) {

        CompletableFuture<T> result = new CompletableFuture<>();
        Consumer<T> whenMatching = v -> {
            if (criteria.test(v)) result.complete(v);
        };
        CompletableFuture.allOf(l.stream()
                        .map(f -> f.thenAccept(whenMatching)).toArray(CompletableFuture<?>[]::new))
                .whenComplete((ignored, t) ->
                        result.completeExceptionally(t != null ? t : new NoSuchElementException()));
        return result;
    }

    @Override
    public Optional<String> apply(int id) {
        var futures = LIBRARY_URLS
                .parallelStream()
                .map(libraryUrl -> CompletableFuture.supplyAsync(() -> restBean.seekInLibrary(id, libraryUrl)))
                .toList();
        return fullFillPromise(anyMatch(futures, Optional::isPresent));
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
