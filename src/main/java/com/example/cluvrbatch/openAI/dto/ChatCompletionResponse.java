package com.example.cluvrbatch.openAI.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(
	List<ChatChoice> choices
) {
}
