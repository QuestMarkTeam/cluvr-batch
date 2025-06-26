package com.example.cluvrbatch.job.reviewRequest.steps;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;
import com.example.cluvrbatch.openai.service.OpenAIService;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewRequestWriter implements ItemWriter<ReviewRequestDto> {

	private final OpenAIService openAIService;
	private final DataSource dataSource;

	@Override
	public void write(Chunk<? extends ReviewRequestDto> chunk) throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		List<Mono<Void>> tasks = StreamSupport.stream(chunk.spliterator(), false)
			.map(item ->
				openAIService.generateChatCompletion(item.getTilContent())

					.doOnNext(result -> {
						item.setFeedback(result.getFeedback());
						item.setSummary(result.getSummary());
						item.setScore(result.getScore());
						item.setReviewed(true);
					})

					// 호출 성공 시
					.flatMap(result ->
						Mono.fromRunnable(() -> {
							jdbcTemplate.update(
								"UPDATE til_reviews SET reviewed = ?, feedback = ?, score = ?, summary = ? WHERE id = ?",
								item.getReviewed(), item.getFeedback(), item.getScore(), item.getSummary(),
								item.getId()
							);
						}).then()
					)

					// 호출 실패 시
					.onErrorResume(e -> {
						log.warn("AI 호출 실패, ID: {}", item.getId(), e);
						item.setFeedback("[AI Failed] 피드백 생성 실패");
						item.setSummary("[AI Failed] 요약 생성 실패");
						item.setScore(0);
						item.setReviewed(true);
						return Mono.fromRunnable(() -> {
							jdbcTemplate.update(
								"UPDATE til_reviews SET reviewed = ?, feedback = ?, score = ?, summary = ? WHERE id = ?",
								item.getReviewed(), item.getFeedback(), item.getScore(), item.getSummary(), item.getId()
							);
						}).then();
					})

			)
			.collect(Collectors.toList());

		CompletableFuture.allOf(
			tasks.stream()
				.map(mono -> mono.toFuture())
				.toArray(CompletableFuture[]::new)
		).get();
	}
}
