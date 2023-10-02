package com.reifocs.playground.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;

@Service("Reactive")
public class Reactive implements SeekFunction {

    private final RestBean restBean;

    public Reactive(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        Executor executor = Executors.newCachedThreadPool();

        return Observable.fromIterable(LIBRARY_URLS)
                .flatMap(libraryUrl ->
                        Observable.fromCallable(() -> restBean.seekInLibrary(id, libraryUrl)))
                .subscribeOn(Schedulers.from(executor))
                .filter(Optional::isPresent).blockingFirst(Optional.empty());
    }
}
