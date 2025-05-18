package com.moyo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Date;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MoyoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoyoBackendApplication.class, args);
    }

}

