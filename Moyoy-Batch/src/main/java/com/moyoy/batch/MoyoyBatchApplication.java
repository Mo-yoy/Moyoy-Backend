package com.moyoy.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.moyoy")
public class MoyoyBatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoyoyBatchApplication.class, args);
	}
}
