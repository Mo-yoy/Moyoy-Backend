package com.moyoy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = "com.moyoy")
@SpringBootApplication(scanBasePackages = {"com.moyoy.api", "com.moyoy.domain", "com.moyoy.infra"})
public class MoyoyApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoyoyApiApplication.class, args);
	}
}
