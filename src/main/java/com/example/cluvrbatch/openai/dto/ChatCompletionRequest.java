package com.example.cluvrbatch.openai.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionRequest(
	String model,
	List<ChatMessage> messages,
	@JsonProperty("max_tokens") int maxTokens,
	double temperature
) {}
