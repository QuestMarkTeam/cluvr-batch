package com.example.cluvrbatch.openAI.config;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
@ConfigurationProperties(prefix = "openai.api")
public class WebClientConfig { ;

	@Value("${OPENAI_API_KEY}")
	private String secretKey;

	private static final String BASE_URL = "https://api.openai.com/v1";

	@Bean
	public WebClient openAIWebClient() {
		return WebClient.builder()
			.baseUrl(BASE_URL)
			.defaultHeader("Authorization", "Bearer " + secretKey)
			.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.build();
	}
}
