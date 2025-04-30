package com.cryptobank.backend.configuration;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DotenvConfig {
    public DotenvConfig() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")  // Look for .env in project root
                .load();
        dotenv.entries().forEach(entry -> {
            System.out.println("Loading: " + entry.getKey() + "=" + entry.getValue());
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
