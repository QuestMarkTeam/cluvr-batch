package com.example.cluvrbatch.job.reactionCount.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.reactionCount.steps.RemoveRedisDataStep.CustomTasklet;

@Configuration
@RequiredArgsConstructor
public class ReactionCountStepConfig {
	private final PlatformTransactionManager platformTransactionManager;
	private final JobRepository jobRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Bean
	public Step reactionCountStep() {
		return new StepBuilder("reactionCountStep", jobRepository)
			.tasklet(new CustomTasklet(redisTemplate), platformTransactionManager)
			.build();
	}
}
