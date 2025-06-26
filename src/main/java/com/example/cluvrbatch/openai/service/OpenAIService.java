package com.example.cluvrbatch.openai.service;

import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cluvrbatch.openai.dto.ChatCompletionRequest;
import com.example.cluvrbatch.openai.dto.ChatCompletionResponse;
import com.example.cluvrbatch.openai.dto.ChatMessage;
import com.example.cluvrbatch.openai.dto.ReviewResultDto;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

	private final WebClient openAIWebClient;

	@Value("${openai.system-message:아래 글을 평가해줘. 반드시 아래 형식을 지켜줘.\n"
		+ "\n"
		+ "형식: [피드백] | [요약] | [점수]\n"
		+ "\n"
		+ "조건:\n"
		+ "- 각 항목은 '|' 문자로 정확히 하나씩만 구분해줘 (총 2개).\n"
		+ "- 피드백과 요약은 200자 이내.\n"
		+ "- 점수는 소수점 없는 정수(Int), 0부터 100 사이.\n"
		+ "- 점수 외에 추가 설명하지 마.\n"
		+ "- 예시처럼 출력해줘: \n"
		+ "예) 글이 명확하나 예시 부족. | 오늘 학습한 내용을 요약함. | 85\n"
		+ "\n"
		+ "절대로 항목을 늘리지 마. 절대로 내가 제시한 형식에서 벗어나지 마.")
	private String systemMessage;

	public Mono<ReviewResultDto> generateChatCompletion(String userPrompt) {
		List<ChatMessage> messages = List.of(
			new ChatMessage("developer", systemMessage),
			new ChatMessage("user", userPrompt)
		);

		ChatCompletionRequest request = new ChatCompletionRequest(
			"gpt-4.1-mini",
			messages,
			300,
			0.2
		);

		return openAIWebClient.post()           // 1-1. HTTP POST 요청 시작
			.uri("/chat/completions")        	// 1-2. 호출할 OpenAI API 경로 설정
			.bodyValue(request)                	// 1-3. 요청 바디에 request 객체 삽입
			.retrieve()                        	// 1-4. 응답 수신 준비

			// 2. 에러 상태 코드 처리 (4xx, 5xx 등)
			.onStatus(status -> status.isError(), response -> {
				log.error("OpenAI API 호출 실패 - Status: {}", response.statusCode());
				return response.bodyToMono(String.class)
					.flatMap(errorBody -> Mono.error(new RuntimeException("OpenAI API 호출 실패: " + errorBody)));
			})

			// 3. 정상 응답 본문을 ChatCompletionResponse 클래스 로 변환
			.bodyToMono(ChatCompletionResponse.class)
			// 요청 시작 로그 출력
			.doOnSubscribe(sub -> log.info("OpenAI API 호출 시작"))
			// 요청 수신 성공 로그 출력
			.doOnNext(res -> log.info("OpenAI API 호출 성공"))

			// 4. 응답에서 실제 텍스트(content) 추출
			.map(response -> {
				if (response.choices() == null || response.choices().isEmpty()) {
					throw new RuntimeException("OpenAI API로부터 응답을 받지 못했습니다");
				}
				return response.choices().get(0).message().content();
			})

			// 5. content 문자열을 ReviewResultDto 객체로 파싱
			.map(ReviewResultDto::parseReviewResult)

			// 실패 시 재시도 로직 (최대 3번, 2초 간격의 지수 백오프)
			.retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
				.filter(throwable -> {
					boolean shouldRetry = !(throwable instanceof IllegalArgumentException);
					if (shouldRetry) {
						log.warn("일시적 오류로 재시도: {}", throwable.getMessage());
					} else {
						log.error("재시도 불가능한 오류: {}", throwable.getMessage());
					}
					return shouldRetry;
				})
			)

			// 최종 실패 시 로그 출력
			.doOnError(error -> log.error("최종 실패: 파싱 또는 OpenAI API 오류, 확인이 필요합니다.", error));
	}
}
