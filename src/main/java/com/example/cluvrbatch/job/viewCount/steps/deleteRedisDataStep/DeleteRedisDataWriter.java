package com.example.cluvrbatch.job.viewCount.steps.deleteRedisDataStep;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class DeleteRedisDataWriter implements ItemWriter<String> {
	private final RedisTemplate<String, Long> redisTemplate;

	@Override
	public void write(@NonNull Chunk<? extends String> chunk) {
		for(String key : chunk.getItems()) {
			redisTemplate.delete(key);
		}
	}
}
