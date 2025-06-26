package com.example.cluvrbatch.job.reviewRequest.steps;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;
import com.example.cluvrbatch.openai.service.OpenAIService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewRequestWriter implements ItemWriter<ReviewRequestDto> {

	private final OpenAIService openAIService;
	private final DataSource dataSource;

	// @Bean
	// public ItemWriter<ReviewRequestDto> reviewRequestWriter(DataSource dataSource) {
	// 	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	// 	return items -> {
	// 		for (ReviewRequestDto item : items) {
	// 			jdbcTemplate.update(
	// 				"UPDATE til_reviews SET reviewed = ?, feedback = ?, score = ?, summary = ? WHERE id = ?",
	// 				item.getReviewed(), item.getFeedback(), item.getScore(), item.getSummary(), item.getId()
	// 			);
	// 		}
	// 	};
	// }

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

					.flatMap(result ->
						Mono.fromRunnable(() -> {
							jdbcTemplate.update(
								"UPDATE til_reviews SET reviewed = ?, feedback = ?, score = ?, summary = ? WHERE id = ?",
								item.getReviewed(), item.getFeedback(), item.getScore(), item.getSummary(),
								item.getId()
							);
						}).then()
					)

					.onErrorResume(e -> {
						log.warn("AI 호출 실패, ID: {}", item.getId(), e);
						return Mono.empty();
					})
			)
			.collect(Collectors.toList());

		Flux.fromIterable(tasks)
			.flatMap(Function.identity(), 5) // 병렬 제한
			.then()
			.block();
	}
}
