package com.rpashev.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Unified API")
                        .version("1.0")
                        .description("This is the unified API combining all endpoints"))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repo")
                        .url("https://github.com/rpashev/java-spring-api"));
    }
}
