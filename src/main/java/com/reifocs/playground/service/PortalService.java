package com.reifocs.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class PortalService {

    public static final List<String> LIBRARY_URLS = IntStream.range(0, 10)
            .mapToObj(i -> "http://localhost:%d/books/".formatted(3000 + i))
            .toList();

    Logger logger = LoggerFactory.getLogger(PortalService.class);

    private SeekFunction seekFunction;
    private final BeanFactory beanFactory;

    public PortalService(SeekFunction seekFunction, BeanFactory beanFactory) {
        this.seekFunction = seekFunction;
        this.beanFactory = beanFactory;
    }


    public Optional<String> getBook(int id) {
        logger.info(seekFunction.getClass().getName());
        return seekFunction.apply(id);
    }

    public String switchAlgorithm(String algo) {
        this.seekFunction = beanFactory.getBean(algo, SeekFunction.class);
        return seekFunction.getClass().getName();
    }

}
