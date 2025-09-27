package com.prj.flashdeal.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Flash Deal API")
                .version("v1.0.0")
                .description("플래시딜 프로젝트 API 명세서");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
