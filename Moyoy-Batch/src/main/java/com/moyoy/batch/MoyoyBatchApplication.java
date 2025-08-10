package com.moyoy.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = "com.moyoy")
@SpringBootApplication(scanBasePackages = {"com.moyoy.batch", "com.moyoy.domain", "com.moyoy.infra"})
public class MoyoyBatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoyoyBatchApplication.class, args);
	}
}
