package com.example.cluvrbatch.job.viewCount;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewCountJobConfig {
	@Bean
	public Job viewCountJob(
		JobRepository jobRepository,
		@Qualifier("viewCountStep") Step viewCountStep,
		@Qualifier("deleteDataOfRedisStep") Step deleteViewCountDataStep
	) {
		return new JobBuilder("viewCountJob", jobRepository)
			.start(viewCountStep)
			.next(deleteViewCountDataStep)
			.build();
	}
}
