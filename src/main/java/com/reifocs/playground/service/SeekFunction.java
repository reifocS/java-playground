package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@FunctionalInterface
@Service
public interface SeekFunction {
    Optional<String> apply(int id);

    default <T> T fullFillPromise(Future<T> prom) {
        try {
            return prom.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Exception promesse - " + e.getMessage());
            return null;
        }
    }
}
