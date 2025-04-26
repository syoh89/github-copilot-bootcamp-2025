package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Demo API")
                .version("v1.0")
                .description("데모 API 문서");
        
        return new OpenAPI()
                .info(info);
    }
}