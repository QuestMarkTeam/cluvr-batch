package com.example.cluvrbatch.job.gemlog.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;


import com.example.cluvrbatch.job.enums.JobStepName;
import com.example.cluvrbatch.job.gemlog.dto.GemLogDto;
import com.example.cluvrbatch.job.gemlog.repository.GemLogJdbcRepository;
import com.example.cluvrbatch.job.gemlog.steps.redisToDbStep.GemLogItemReader;
import com.example.cluvrbatch.job.gemlog.steps.redisToDbStep.GemLogItemWriter;

@Configuration
@RequiredArgsConstructor
public class GemLogStepConfig {

	private final PlatformTransactionManager platformTransactionManager;

	private final RedisTemplate<String, String> redisTemplate;
	private final GemLogJdbcRepository gemLogJdbcRepository;

	@Bean
	public Step gemEventStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		ItemReader<GemLogDto> gemLogReader,
		ItemWriter<GemLogDto> gemLogWriter) { // Job을 구성하는 단일 Step 단계 정의 Step = 그 안에서 실행되는 한 단계 (ex. Redis → RDS 저장)
		return new StepBuilder(JobStepName.GEM_LOG_JOB.name(), jobRepository)
			.<GemLogDto, GemLogDto>chunk(1000, transactionManager)
			.reader(gemLogReader)
			.writer(gemLogWriter)
			.build();
	}

	@Bean
	public ItemReader<GemLogDto> gemLogReader() {
		return new GemLogItemReader(redisTemplate);
	}

	@Bean
	public ItemWriter<GemLogDto> gemLogWriter() {
		return new GemLogItemWriter(gemLogJdbcRepository);
	}
}
