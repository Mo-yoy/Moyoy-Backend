package com.moyoy.infra.external.support.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.moyoy.infra"})
public class FeignClientConfig {}
