package com.kaaj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kaaj", "com.kaaj.api"})
public class KaajBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaajBackendApplication.class, args);
    }

}