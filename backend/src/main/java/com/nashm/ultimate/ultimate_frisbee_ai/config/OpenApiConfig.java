package com.nashm.ultimate.ultimate_frisbee_ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ultimateFrisbeeAiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ultimate Frisbee AI API")
                        .description("API for interacting with the Ultimate Frisbee AI Assistant")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Nash")
                                .email("no@no.com")));
    }
}
