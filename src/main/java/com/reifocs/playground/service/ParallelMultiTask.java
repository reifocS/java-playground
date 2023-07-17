package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

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

    @Override
    public Optional<String> apply(int id) {
        final CountDownLatch doneSignal = new CountDownLatch(LIBRARY_URLS.size());
        final AtomicReference<Optional<String>> result = new AtomicReference<>();
        for (String libraryUrl : LIBRARY_URLS) {
            restBean.statefulAsync(id, doneSignal, result, libraryUrl);
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }


}
