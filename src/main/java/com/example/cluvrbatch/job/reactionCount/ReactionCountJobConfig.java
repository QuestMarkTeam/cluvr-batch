package com.example.cluvrbatch.job.reactionCount;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.cluvrbatch.job.reactionCount.steps.ReactionCountStepConfig;

@Configuration
@RequiredArgsConstructor
public class ReactionCountJobConfig {
	private final JobRepository jobRepository;
	private final ReactionCountStepConfig reactionCountStepConfig;

	@Bean
	public Job reactionCountJob() {
		return new JobBuilder("reactionCountJob", jobRepository)
			.start(reactionCountStepConfig.reactionCountStep())
			.build();
	}
}
