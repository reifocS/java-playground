package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.reifocs.playground.service.PortalService.LIBRARY_URLS;


@Service("SequentialMultiTask")
public class SequentialMultiTask implements SeekFunction {

    private final RestBean restBean;

    public SequentialMultiTask(RestBean restBean) {
        this.restBean = restBean;
    }

    @Override
    public Optional<String> apply(int id) {
        final CountDownLatch countDownLatch = new CountDownLatch(LIBRARY_URLS.size());

        final ExecutorService executorService = Executors.newCachedThreadPool();

        final AtomicReference<Optional<String>> result = new AtomicReference<>();

        for (String libraryUrl : LIBRARY_URLS) {

            executorService.submit(() -> {

                final Optional<String> ressource =
                        restBean.seekInLibrary(id, libraryUrl);


                if (ressource.isEmpty()) {
                    countDownLatch.countDown();
                } else {
                    result.set(ressource);

                    while (countDownLatch.getCount() > 0) {
                        countDownLatch.countDown();
                    }
                }

            });
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        return result.get();
    }


}
