package com.example.cluvrbatch.job.cloverlog;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.cloverlog.dto.CloverEventResponseDto;
import com.example.cluvrbatch.job.enums.RedisKey;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CloverLogItemReader implements ItemReader<CloverEventResponseDto> {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private Queue<CloverEventResponseDto> cached = new LinkedList<>();

	@Override
	public CloverEventResponseDto read() throws Exception {
		if (cached.isEmpty()) {
			Set<String> keys = redisTemplate.keys(RedisKey.CLOVER_LOG.getKey() + "*");

			for (String key : keys) {
				List<String> logs = redisTemplate.opsForList().range(key, 0, -1);
				for (String json : logs) {
					cached.add(objectMapper.readValue(json, CloverEventResponseDto.class));
				}
				redisTemplate.delete(key); // 삭제
			}
		}

		return cached.poll(); // null 반환 시 자동 종료
	}
}
