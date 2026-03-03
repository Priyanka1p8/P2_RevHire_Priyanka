package com.rev.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RevhireP2Application {

    public static void main(String[] args) {
        SpringApplication.run(RevhireP2Application.class, args);
    }

}
