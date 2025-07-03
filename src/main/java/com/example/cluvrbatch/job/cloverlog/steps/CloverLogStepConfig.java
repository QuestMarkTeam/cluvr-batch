package com.example.cluvrbatch.job.cloverlog.steps;

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

import com.example.cluvrbatch.job.cloverlog.dto.CloverLogDto;
import com.example.cluvrbatch.job.cloverlog.repository.CloverLogJdbcRepository;
import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemReader;
import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemWriter;
import com.example.cluvrbatch.job.enums.JobStepName;

@Configuration
@RequiredArgsConstructor
public class CloverLogStepConfig {

	private final RedisTemplate<String, String> redisTemplate;
	private final CloverLogJdbcRepository cloverLogJdbcRepository;

	@Bean
	public Step cloverEventStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		ItemReader<CloverLogDto> cloverLogReader,
		ItemWriter<CloverLogDto> cloverLogWriter) { // Job을 구성하는 단일 Step 단계 정의 Step = 그 안에서 실행되는 한 단계 (ex. Redis → RDS 저장)
		return new StepBuilder(JobStepName.CLOVER_LOG_JOB.name(), jobRepository)
			.<CloverLogDto, CloverLogDto>chunk(1000, transactionManager)
			.reader(cloverLogReader)
			.writer(cloverLogWriter)
			.build();
	}

	@Bean
	public ItemReader<CloverLogDto> cloverLogReader() {
		return new CloverLogItemReader(redisTemplate);
	}

	@Bean
	public ItemWriter<CloverLogDto> cloverLogWriter() {
		return new CloverLogItemWriter(cloverLogJdbcRepository);
	}
}
