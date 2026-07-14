package com.shopease.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enables {@code @ConfigurationProperties} record binding across the app.
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = "com.shopease.config")
public class PropertiesConfig {
}
