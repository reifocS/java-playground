package com.reifocs.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaygroundApplication.class, args);
    }

}
