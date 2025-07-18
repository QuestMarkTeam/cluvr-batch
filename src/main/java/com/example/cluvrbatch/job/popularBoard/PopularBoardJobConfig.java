package com.example.cluvrbatch.job.popularBoard;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.cluvrbatch.job.popularBoard.steps.PopularBoardStepConfig;

@Configuration
@RequiredArgsConstructor
public class PopularBoardJobConfig {
	private final JobRepository jobRepository;
	private final PopularBoardStepConfig popularBoardStepConfig;

	@Bean
	public Job popularBoardJob() {
		return new JobBuilder("popularBoardJob", jobRepository)
				.start(popularBoardStepConfig.getPopularBoardIdsFromRedisStep())
				.next(popularBoardStepConfig.updatePopularBoardRedisDataStep())
				.build();
	}


}
