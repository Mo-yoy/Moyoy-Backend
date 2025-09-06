package com.moyoy.infra.database.mysql.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EnableJpaRepositories(basePackages = "com.moyoy.infra")
@EntityScan(basePackages = "com.moyoy.infra")
@EnableJpaAuditing
public class JpaTestConfig {

}
