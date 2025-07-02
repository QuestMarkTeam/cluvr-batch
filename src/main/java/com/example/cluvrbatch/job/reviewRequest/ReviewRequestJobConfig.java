package com.example.cluvrbatch.job.reviewRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.enums.JobStepName;
import com.example.cluvrbatch.job.reviewRequest.steps.ReviewRequestTasklet;


@Configuration
@RequiredArgsConstructor
public class ReviewRequestJobConfig {

	private final ReviewRequestTasklet reviewRequestTasklet;

	@Bean
	public Job reviewRequestJob(JobRepository jobRepository, Step reviewStep) {
		return new JobBuilder(JobStepName.REVIEW_REQUEST_JOB.name(), jobRepository)
			.start(reviewStep)
			.build();
	}

	@Bean
	public Step reviewStep(JobRepository jobRepository,
		PlatformTransactionManager txManager
	) {
		return new StepBuilder(JobStepName.REVIEW_REQUEST_JOB.name(), jobRepository)
			.tasklet(reviewRequestTasklet, txManager)
			.build();
	}
}
