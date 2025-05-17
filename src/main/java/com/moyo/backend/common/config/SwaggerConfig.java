package com.moyo.backend.common.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.moyo.backend.common.constant.MoyoConstants.AUTHORIZATION;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI moyoOpenAPI() {

        return new OpenAPI()
                .info(apiInfo())
                .components(auth());
    }

    private Info apiInfo(){
        return new Info().title("Moyoy Server API")
                         .description("Moyo Server API 명세서")
                         .version("v1.0.4");
    }

    private Components auth(){
        return new Components()

                .addSecuritySchemes("jwt_access", new SecurityScheme()
                                .type(HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")
                                .in(HEADER)
                                .name(AUTHORIZATION));

//                .addSecuritySchemes("refresh", new SecurityScheme()
//                                .type(SecurityScheme.Type.APIKEY)
//                                .in(SecurityScheme.In.COOKIE)
//                                .name("refresh"));
    }

}
