package com.example.cluvrbatch.job.viewCount.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;
import com.example.cluvrbatch.job.viewCount.repository.BoardJdbcRepository;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.RedisViewCountReader;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.ViewCountProcessor;
import com.example.cluvrbatch.job.viewCount.steps.redisToDbStep.ViewCountWriter;

@Configuration
@RequiredArgsConstructor
public class ViewCountStepConfig {
	private final PlatformTransactionManager platformTransactionManager;


	private final RedisTemplate<String, Long> redisTemplate;
	private final BoardJdbcRepository boardJdbcRepository;

	@Bean
	public Step viewCountStep(JobRepository jobRepository) {
		return new StepBuilder("viewCountStep", jobRepository)
			.<BoardViewCount, BoardViewCount>chunk(1000, platformTransactionManager)
			.reader(viewCountReader())
			.processor(viewCountProcessor())
			.writer(viewCountWriter())
			.build();
	}

	@Bean
	public ItemReader<BoardViewCount> viewCountReader() {
		return new RedisViewCountReader(redisTemplate);
	}

	@Bean
	public ItemProcessor<BoardViewCount, BoardViewCount> viewCountProcessor() {
		return new ViewCountProcessor();
	}

	@Bean
	public ItemWriter<BoardViewCount> viewCountWriter() {
		return new ViewCountWriter(boardJdbcRepository);
	}
}
