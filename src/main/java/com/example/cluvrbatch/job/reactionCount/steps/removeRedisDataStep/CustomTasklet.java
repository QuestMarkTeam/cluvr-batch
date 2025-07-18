package com.example.cluvrbatch.job.reactionCount.steps.removeRedisDataStep;

import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class CustomTasklet implements Tasklet {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		Set<String> keys = redisTemplate.keys("reaction:*");

		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
			System.out.println("🧹 Deleted keys: " + keys);
		} else {
			System.out.println("ℹ️ No keys to delete.");
		}
		return RepeatStatus.FINISHED;
	}
}
