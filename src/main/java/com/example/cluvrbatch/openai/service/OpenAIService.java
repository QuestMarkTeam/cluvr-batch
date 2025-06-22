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
			.bodyToMono(ChatCompletionResponse.class)
			.map(response -> {
				return response.choices().get(0).message().content();
			});
	}
}
