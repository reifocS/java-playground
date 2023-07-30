package com.reifocs.playground.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;


@Service("ParallelMultiTask")
public class ParallelMultiTask implements SeekFunction {

    private final RestBean restBean;

    public ParallelMultiTask(RestBean restBean) {
        this.restBean = restBean;
    }


    public Mono<Optional> exampleAsyncApiCall(String url, ExchangeFilterFunction callback) {
        return WebClient.builder() // Ideally this should be pre-defined somewhere else and not created every time
                .filters(exchangeFilterFunctions -> exchangeFilterFunctions.add(callback))
                .build()
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Optional.class);
    }

    @Override
    public Optional<String> apply(int id) {
        final CountDownLatch doneSignal = new CountDownLatch(LIBRARY_URLS.size());
        final AtomicReference<Optional<String>> result = new AtomicReference<>();
        //TODO use WebClient
        for (String libraryUrl : LIBRARY_URLS) {
            ExchangeFilterFunction callback = ExchangeFilterFunction.ofResponseProcessor(clientResponse ->
                    Mono.defer(() -> {
                        System.out.println(clientResponse);
                        doneSignal.countDown();
                        return Mono.just(clientResponse);
                    }));
            var mono = exampleAsyncApiCall(libraryUrl + id, callback);
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }


}
