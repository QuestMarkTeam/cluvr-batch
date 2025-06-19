package com.example.cluvrbatch.job.cloverlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.cloverlog.dto.CloverEventResponseDto;
import com.example.cluvrbatch.job.enums.JobStepName;

@Configuration
@RequiredArgsConstructor
public class CloverLogJobConfig {

	private final CloverLogItemReader reader;
	private final CloverLogItemWriter writer;

	@Bean
	public Job cloverLogJob(JobRepository jobRepository,
		Step cloverEventStep) { // 하나의 배치 Job 단위 전체 흐름 정의 Job = 전체 배치 단위
		return new JobBuilder(JobStepName.CLOVER_LOG_JOB.name(), jobRepository)
			.start(cloverEventStep)
			.build();
	}

	@Bean
	public Step cloverEventStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		ItemReader<CloverEventResponseDto> reader,
		ItemWriter<CloverEventResponseDto> writer) { // Job을 구성하는 단일 Step 단계 정의 Step = 그 안에서 실행되는 한 단계 (ex. Redis → RDS 저장)
		return new StepBuilder(JobStepName.CLOVER_LOG_JOB.name(), jobRepository)
			.<CloverEventResponseDto, CloverEventResponseDto>chunk(1000, transactionManager)
			.reader(reader)
			.writer(writer)
			.build();
	}
}
