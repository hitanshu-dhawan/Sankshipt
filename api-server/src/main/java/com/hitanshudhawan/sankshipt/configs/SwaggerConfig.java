package com.hitanshudhawan.sankshipt.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * This class configures the API documentation for the Sankshipt URL shortener application.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sankshipt API")
                        .description(
                                "Sankshipt – A simple and efficient URL shortener built with Java Spring Boot. "
                                        + "It generates short links, tracks clicks with metadata (IP, browser, etc.), "
                                        + "and provides analytics for your URLs."
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("sankshipt-api")
                .packagesToScan("com.hitanshudhawan.sankshipt.controllers")
                .build();
    }

}
