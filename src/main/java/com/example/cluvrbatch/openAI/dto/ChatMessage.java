package com.example.cluvrbatch.openAI.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMessage(
	String role,
	String content
) {}
