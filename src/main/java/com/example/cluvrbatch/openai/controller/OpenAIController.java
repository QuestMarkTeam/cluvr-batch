package com.example.cluvrbatch.openai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrbatch.openai.dto.GeneratePromptDto;
import com.example.cluvrbatch.openai.dto.ReviewResultDto;
import com.example.cluvrbatch.openai.service.OpenAIService;

import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

	private final OpenAIService openAIService;

	@PostMapping("/chat_completion")
	public Mono<ReviewResultDto> chat(@RequestBody GeneratePromptDto dto) {
		return openAIService.generateChatCompletion(dto.prompt())
			.onErrorResume(e -> {
				log.error("OpenAI API 호출 중 오류 발생", e);
				ReviewResultDto fallback = new ReviewResultDto("오류 발생", "요약 불가", 0);
				return Mono.just(fallback);
			});
	}
}
