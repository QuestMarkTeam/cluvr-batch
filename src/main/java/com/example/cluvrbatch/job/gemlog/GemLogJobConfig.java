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
import com.example.cluvrbatch.job.gemlog.dto.GemLogDto;
import com.example.cluvrbatch.job.gemlog.steps.redisToDbStep.GemLogItemReader;
import com.example.cluvrbatch.job.gemlog.steps.redisToDbStep.GemLogItemWriter;

@Configuration
@RequiredArgsConstructor
public class GemLogJobConfig {

	@Bean
	public Job gemLogJob(JobRepository jobRepository, Step gemEventStep) { // 하나의 배치 Job 단위 전체 흐름 정의 Job = 전체 배치 단위
		return new JobBuilder(JobStepName.GEM_LOG_JOB.name(), jobRepository)
			.start(gemEventStep)
			.build();
	}

}
