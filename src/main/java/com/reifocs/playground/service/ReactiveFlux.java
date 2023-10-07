package com.reifocs.playground.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("ReactiveFlux")
public class ReactiveFlux implements SeekFunction {

    private final ReactiveClient client;

    public ReactiveFlux(ReactiveClient client) {
        this.client = client;
    }

    @Override
    public Optional<String> apply(int id) {

        /*return Flux.fromIterable(LIBRARY_URLS)
                .parallel() // Enable parallel processing
                .runOn(Schedulers.fromExecutor(executor))
                .flatMap(libraryUrl -> Mono.fromCallable(() -> restBean.seekInLibrary(id, libraryUrl)))
                .filter(Optional::isPresent)
                .sequential().blockFirst();*/
        throw new RuntimeException();
    }

    public Mono<String> findById(int id) {
        return Flux.fromIterable(LIBRARY_URLS)
                .flatMap(libraryUrl ->
                        client.seekInLibrary(id, libraryUrl)).filter(b -> !b.isEmpty()).single();
    }
}
