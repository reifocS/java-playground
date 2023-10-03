package com.reifocs.playground.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("ReactiveFlux")
public class ReactiveFlux implements SeekFunction {

    private final ReactiveClient client;
    private final RestBean restBean;

    public ReactiveFlux(ReactiveClient client, RestBean restBean) {
        this.client = client;
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        Executor executor = Executors.newCachedThreadPool();

        return Flux.fromIterable(LIBRARY_URLS)
                .parallel() // Enable parallel processing
                .runOn(Schedulers.fromExecutor(executor))
                .flatMap(libraryUrl -> Mono.fromCallable(() -> restBean.seekInLibrary(id, libraryUrl)))
                .filter(Optional::isPresent)
                .sequential().blockFirst();
    }

    public Mono<String> findById(int id) {
        return Flux.fromIterable(LIBRARY_URLS)
                .flatMap(libraryUrl ->
                        client.seekInLibrary(id, libraryUrl)).filter(b -> !b.isEmpty()).single();
    }
}
