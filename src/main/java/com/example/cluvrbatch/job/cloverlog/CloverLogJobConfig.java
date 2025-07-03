package com.example.cluvrbatch.job.cloverlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemReader;
import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemWriter;
import com.example.cluvrbatch.job.enums.JobStepName;

@Configuration
@RequiredArgsConstructor
public class CloverLogJobConfig {

	@Bean
	public Job cloverLogJob(JobRepository jobRepository,
		Step cloverEventStep) { // 하나의 배치 Job 단위 전체 흐름 정의 Job = 전체 배치 단위
		return new JobBuilder(JobStepName.CLOVER_LOG_JOB.name(), jobRepository)
			.start(cloverEventStep)
			.build();
	}


}
