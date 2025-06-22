package com.example.cluvrbatch.openai.controller;

import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrbatch.openai.dto.GeneratePromptDto;
import com.example.cluvrbatch.openai.service.OpenAIService;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

	private final OpenAIService openAIService;

	@PostMapping("/chat_completion")
	public Mono<ResponseEntity<String>> chat(@RequestBody GeneratePromptDto dto) {
		return openAIService.generateChatCompletion(dto.prompt())
			.map(response -> ResponseEntity.ok().body(response))
			.onErrorResume(e -> Mono.just(
				ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error: " + e.getMessage())
			));
	}
}
