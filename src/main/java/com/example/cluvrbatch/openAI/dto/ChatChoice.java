package com.example.cluvrbatch.openAI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatChoice(
	int index,
	ChatMessage message,
	@JsonProperty("logprobs") Object logprobs,
	@JsonProperty("finish_reason") String finishReason
) {}
