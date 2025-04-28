package com.cryptobank.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BeCryptoBankApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeCryptoBankApplication.class, args);
	}
}