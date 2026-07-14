package com.shopease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Shopease — Luxury E-Commerce API.
 *
 * <p>Entry point for the Spring Boot 3 / Java 21 backend.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class ShopeaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopeaseApplication.class, args);
    }
}
