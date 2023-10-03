package com.reifocs.playground.controller;

import com.reifocs.playground.service.ReactivePortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reactive")
public class ReactivePortalController {

    private final ReactivePortalService reactivePortalService;

    public ReactivePortalController(ReactivePortalService portalService) {
        this.reactivePortalService = portalService;
    }

    @GetMapping("/seek/{id}")
    public Mono<String> seekBook(@PathVariable int id) {
        return reactivePortalService.getBook(id);
    }

}
