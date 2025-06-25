package com.example.cluvrbatch.job.viewCount;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ViewCountJobConfig {
	private final JobRepository jobRepository;
	private final Step viewCountStep;

	@Bean
	public Job viewCountJob() {
		return new JobBuilder("viewCountJob", jobRepository)
			.start(viewCountStep)
			.build();
	}
}
