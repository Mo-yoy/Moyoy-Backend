package com.moyoy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.moyoy")
public class MoyoyApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoyoyApiApplication.class, args);
	}
}
