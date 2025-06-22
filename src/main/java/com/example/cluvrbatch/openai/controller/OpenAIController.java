package com.example.cluvrbatch.openai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.cluvrbatch.openai.dto.GeneratePromptDto;
import com.example.cluvrbatch.openai.service.OpenAIService;

import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

	private final OpenAIService openAIService;

	@PostMapping("/chat_completion")
	public Mono<ResponseEntity<String>> chat(@RequestBody GeneratePromptDto dto) {
		return openAIService.generateChatCompletion(dto.prompt())
			.map(response -> ResponseEntity.ok().body(response))
			.onErrorResume(e -> {
				log.error("OpenAI API 호출 중 오류 발생", e);
				if (e instanceof WebClientResponseException) {
					return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body("OpenAI 서비스 일시적 오류: " + e.getMessage()));
				}
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("내부 서버 오류가 발생했습니다"));
			});
	}
}
