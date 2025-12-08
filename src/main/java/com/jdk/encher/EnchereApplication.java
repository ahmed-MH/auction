package com.jdk.encher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnchereApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnchereApplication.class, args);
    }

}
