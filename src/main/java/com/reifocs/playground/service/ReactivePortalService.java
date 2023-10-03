package com.reifocs.playground.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactivePortalService {
    private final ReactiveFlux seekFunction;

    public ReactivePortalService(ReactiveFlux seekFunction) {
        this.seekFunction = seekFunction;
    }


    public Mono<String> getBook(int id) {
        return seekFunction.findWithFlux(id);
    }

}
