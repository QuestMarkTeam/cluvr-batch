package com.example.cluvrbatch.job.viewCount.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;
import com.example.cluvrbatch.job.viewCount.repository.BoardJdbcRepository;
import com.example.cluvrbatch.job.viewCount.steps.deleteRedisDataStep.DeleteRedisDataReader;
import com.example.cluvrbatch.job.viewCount.steps.deleteRedisDataStep.DeleteRedisDataWriter;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.RedisToDbStepReader;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.RedisToDbStepProcessor;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.RedisToDbStepWriter;

@Configuration
@RequiredArgsConstructor
public class ViewCountStepConfig {
	private final PlatformTransactionManager platformTransactionManager;
	private final StringRedisTemplate redisTemplate;
	private final RedisTemplate<String, Long> redisLongTemplate;
	private final BoardJdbcRepository boardJdbcRepository;

	@Bean
	public Step viewCountStep(JobRepository jobRepository) {
		return new StepBuilder("viewCountStep", jobRepository)
			.<BoardViewCount, BoardViewCount>chunk(1000, platformTransactionManager)
			.reader(new RedisToDbStepReader(redisTemplate, redisLongTemplate))
			.processor(new RedisToDbStepProcessor(redisLongTemplate))
			.writer(new RedisToDbStepWriter(boardJdbcRepository))
			.build();
	}

	@Bean
	public Step deleteDataOfRedisStep(JobRepository jobRepository) {
		return new StepBuilder("deleteViewCountDataStep", jobRepository)
			.<String, String>chunk(1000, platformTransactionManager)
			.reader(new DeleteRedisDataReader())
			.writer(new DeleteRedisDataWriter(redisLongTemplate))
			.build();
	}
}

//인기게시글, 유저 id 지우는 작업, 조하요/싫어요 수

//데이터가 많아서

