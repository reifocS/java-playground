package com.reifocs.playground.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@FunctionalInterface
@Service
public interface SeekFunction {
    Optional<String> apply(int id);
}
