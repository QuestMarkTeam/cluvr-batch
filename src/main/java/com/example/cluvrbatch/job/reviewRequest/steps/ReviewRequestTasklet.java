package com.example.cluvrbatch.job.reviewRequest.steps;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;
import com.example.cluvrbatch.job.reviewRequest.entity.TilReview;
import com.example.cluvrbatch.job.reviewRequest.repository.TilReviewRepository;
import com.example.cluvrbatch.openai.service.OpenAIService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewRequestTasklet implements Tasklet {

	private final TilReviewRepository tilReviewRepository;
	private final OpenAIService openAIService;
	private final JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// 1. 리뷰할 TIL들 불러오기
		List<ReviewRequestDto> requests = tilReviewRepository.findAllByReviewedIsFalse();

		// 2. 비동기 병렬 호출 + DB 업데이트
		Flux.fromIterable(requests)
			.flatMap(item -> openAIService.generateChatCompletion(item.getTilContent())
					// 성공 시 성공 값 저장
					.doOnNext(result -> {
						item.setFeedback(result.getFeedback());
						item.setSummary(result.getSummary());
						item.setScore(result.getScore());
						item.setReviewed(true);
					})
					.flatMap(res -> updateToDb(item))
					// 실패 시 실패 값 저장
					.onErrorResume(e -> {
						log.warn("OpenAI 호출 실패, id={}", item.getId(), e);
						item.setFeedback("[AI Failed] 피드백 생성 실패");
						item.setSummary("[AI Failed] 요약 생성 실패");
						item.setScore(0);
						item.setReviewed(true);
						return updateToDb(item);
					})
				, 10)
			.then()
			.block();

		log.info("총 {}건 리뷰 완료", requests.size());
		return RepeatStatus.FINISHED;
	}

	private Mono<Void> updateToDb(ReviewRequestDto item) {
		return Mono.fromRunnable(() -> {
			TilReview review = tilReviewRepository.findById(item.getId())
				.orElseThrow(() -> new IllegalArgumentException("리뷰 엔티티를 찾을 수 없습니다. ID=" + item.getId()));

			review.updateFeedback(item.getFeedback());
			review.updateSummary(item.getSummary());
			review.updateScore(item.getScore());
			review.updateReviewed(item.getReviewed());

			tilReviewRepository.save(review); // 변경 감지 + save
		});
	}
}
