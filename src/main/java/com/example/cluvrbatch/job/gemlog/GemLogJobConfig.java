package com.example.cluvrbatch.job.gemlog;

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

import com.example.cluvrbatch.job.enums.JobStepName;
import com.example.cluvrbatch.job.gemlog.dto.GemEventResponseDto;

@Configuration
@RequiredArgsConstructor
public class GemLogJobConfig {

	private final GemLogItemReader reader;
	private final GemLogItemWriter writer;

	@Bean
	public Job gemLogJob(JobRepository jobRepository, Step gemEventStep) { // 하나의 배치 Job 단위 전체 흐름 정의 Job = 전체 배치 단위
		return new JobBuilder(JobStepName.GEM_LOG_JOB.name(), jobRepository)
			.start(gemEventStep)
			.build();
	}

	@Bean
	public Step gemEventStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		ItemReader<GemEventResponseDto> reader,
		ItemWriter<GemEventResponseDto> writer) { // Job을 구성하는 단일 Step 단계 정의 Step = 그 안에서 실행되는 한 단계 (ex. Redis → RDS 저장)
		return new StepBuilder(JobStepName.GEM_LOG_JOB.name(), jobRepository)
			.<GemEventResponseDto, GemEventResponseDto>chunk(1000, transactionManager)
			.reader(reader)
			.writer(writer)
			.build();
	}
}
