package com.example.cluvrbatch.job.reviewRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemReader;
import com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep.CloverLogItemWriter;
import com.example.cluvrbatch.job.enums.JobStepName;
import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;
import com.example.cluvrbatch.job.reviewRequest.steps.ReviewRequestReader;
import com.example.cluvrbatch.job.reviewRequest.steps.ReviewRequestWriter;

@Configuration
@RequiredArgsConstructor
public class ReviewRequestJobConfig {

	private final ReviewRequestReader reader;
	private final ReviewRequestWriter writer;

	@Bean
	public Job reviewRequestJob(JobRepository jobRepository, Step reviewStep) {
		return new JobBuilder(JobStepName.REVIEW_REQUEST_JOB.name(), jobRepository)
			.start(reviewStep)
			.build();
	}

	@Bean
	public Step reviewStep(JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		ItemReader<ReviewRequestDto> reviewRequestReader,
		ItemWriter<ReviewRequestDto> reviewRequestWriter) {

		return new StepBuilder(JobStepName.REVIEW_REQUEST_JOB.name(), jobRepository)
			.<ReviewRequestDto, ReviewRequestDto>chunk(10, transactionManager)
			.reader(reviewRequestReader)
			.writer(reviewRequestWriter)
			.build();
	}
}
