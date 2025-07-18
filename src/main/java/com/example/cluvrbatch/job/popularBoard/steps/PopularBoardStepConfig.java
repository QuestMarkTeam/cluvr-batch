package com.example.cluvrbatch.job.popularBoard.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.popularBoard.repository.BoardJdbcRepository;
import com.example.cluvrbatch.job.popularBoard.steps.getPopularBoardsIdsFromRedisStep.GetPopularBoardsIdsTasklet;
import com.example.cluvrbatch.job.popularBoard.steps.updatePopularBoardRedisDataStep.UpdatePopularBoardTasklet;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class PopularBoardStepConfig {
	private final PlatformTransactionManager platformTransactionManager;
	private final JobRepository jobRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final BoardJdbcRepository boardJdbcRepository;
	private final ObjectMapper objectMapper;

	@Bean
	public Step getPopularBoardIdsFromRedisStep() {
		return new StepBuilder("getPopularBoardIdsFromRedisStep", jobRepository)
			.tasklet(new GetPopularBoardsIdsTasklet(redisTemplate), platformTransactionManager)
			.build();
	}

	@Bean
	public Step updatePopularBoardRedisDataStep() {
		return new StepBuilder("updatePopularBoardRedisDataStep", jobRepository)
			.tasklet(new UpdatePopularBoardTasklet(boardJdbcRepository, redisTemplate, objectMapper), platformTransactionManager)
			.build();
	}
}
