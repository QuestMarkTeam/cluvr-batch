package com.example.cluvrbatch.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratePromptDto(
	String prompt
) {
}
