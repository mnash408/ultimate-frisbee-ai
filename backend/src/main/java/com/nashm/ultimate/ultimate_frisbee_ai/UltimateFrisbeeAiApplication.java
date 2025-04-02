package com.nashm.ultimate.ultimate_frisbee_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nashm.ultimate.ultimate_frisbee_ai"})
public class UltimateFrisbeeAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UltimateFrisbeeAiApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}


