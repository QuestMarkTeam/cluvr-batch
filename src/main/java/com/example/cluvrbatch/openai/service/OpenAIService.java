package com.example.cluvrbatch.openai.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cluvrbatch.openai.dto.ChatCompletionRequest;
import com.example.cluvrbatch.openai.dto.ChatCompletionResponse;
import com.example.cluvrbatch.openai.dto.ChatMessage;

import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

	private final WebClient openAIWebClient;

	public Mono<String> generateChatCompletion(String userPrompt) {
		List<ChatMessage> messages = List.of(
			new ChatMessage("developer", "아래의 TIL 을 100자 이하로 간단하게 요약해주고, 너의 피드백과 함께 10점이 만점으로 채점해. 총300자 이하로 부탁해"),
			new ChatMessage("user", userPrompt)
		);

		ChatCompletionRequest request = new ChatCompletionRequest(
			"gpt-4.1-nano",
			messages,
			300,
			0.2
		);

		return openAIWebClient.post()
			.uri("/chat/completions") // baseUrl은 config에서 지정됨
			.bodyValue(request)
			.retrieve()
			.onStatus(status -> status.isError(), response -> {
				log.error("OpenAI API 호출 실패 - Status: {}", response.statusCode());
				return response.bodyToMono(String.class)
					.flatMap(errorBody -> Mono.error(new RuntimeException("OpenAI API 호출 실패: " + errorBody)));
			})
			.bodyToMono(ChatCompletionResponse.class)
			.doOnSubscribe(subscription -> log.info("OpenAI API 호출 시작"))
			.doOnNext(response -> log.info("OpenAI API 호출 성공"))
			.doOnError(error -> log.error("OpenAI API 호출 중 오류 발생", error))
			.map(response -> {
				if (response.choices() == null || response.choices().isEmpty()) {
					throw new RuntimeException("OpenAI API로부터 응답을 받지 못했습니다");
				}
				return response.choices().get(0).message().content();
			});
	}
}
