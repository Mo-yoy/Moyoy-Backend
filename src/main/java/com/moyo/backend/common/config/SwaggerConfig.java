package com.moyo.backend.common.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.moyo.backend.common.constant.MoyoConstants.BEARER;
import static com.moyo.backend.common.constant.MoyoConstants.JWT;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI moyoOpenAPI() {

        return new OpenAPI()
                .info(createInfo())
                .components(new Components().addSecuritySchemes(JWT,createJwtConfig()));
    }

    private Info createInfo(){

        return new Info()
                .title("Moyo Server API")
                .description("Moyo Server API 명세서")
                .version("v1.0.0");
    }
    private SecurityScheme createJwtConfig(){

        return new SecurityScheme()
                .type(HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT);
    }
}
