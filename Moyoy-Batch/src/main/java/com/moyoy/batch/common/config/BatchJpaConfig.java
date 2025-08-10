package com.moyoy.batch.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.moyoy.batch.jobRepository")
@EntityScan(basePackages = "com.moyoy.batch.jobRepository")
public class BatchJpaConfig {
}
