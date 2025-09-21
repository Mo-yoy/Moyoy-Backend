package com.moyoy.infra.database.mysql.support.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
// @EntityScan(basePackages = "com.moyoy.infra")
// @EnableJpaRepositories(basePackages = "com.moyoy.infra")
@EntityScan("com.moyoy") ///  TODO : API 모듈 리프레시 토큰 임시 RDB 저장, 배치 애플리케이션 Spring Batch 도입전 Meta 데이터 직접 관리로 임시 설정
@EnableJpaRepositories("com.moyoy")
public class JpaConfig {}
