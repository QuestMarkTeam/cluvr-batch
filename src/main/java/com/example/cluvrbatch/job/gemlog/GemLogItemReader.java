package com.example.cluvrbatch.job.gemlog;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.enums.RedisKey;
import com.example.cluvrbatch.job.gemlog.dto.GemEventResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class GemLogItemReader implements ItemReader<GemEventResponseDto> {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private Queue<GemEventResponseDto> cached = new LinkedList<>();

	@Override
	public GemEventResponseDto read() throws Exception {
		if (cached.isEmpty()) {
			Set<String> keys = redisTemplate.keys(RedisKey.GEM_LOG.getKey() + "*");

			for (String key : keys) {
				List<String> logs = redisTemplate.opsForList().range(key, 0, -1);
				for (String json : logs) {
					cached.add(objectMapper.readValue(json, GemEventResponseDto.class));
				}
				redisTemplate.delete(key); // 삭제
			}
		}

		return cached.poll(); // null 반환 시 자동 종료
	}
}
