package com.example.cluvrbatch.job.gemlog.steps.redisToDbStep;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.enums.RedisKey;
import com.example.cluvrbatch.job.gemlog.dto.GemLogDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GemLogItemReader implements ItemReader<GemLogDto> {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Queue<GemLogDto> cached = new LinkedList<>();
	private Queue<String> unprocessedData = new LinkedList<>(); // 에러 발생한 데이터를 추적할 큐
	private static final int CACHE_LIMIT = 1000; // 캐시 크기 제한 (최대 1000개 항목)
	private static final int BATCH_SIZE = 100; // 한 번에 가져올 데이터의 수 (성능 최적화)
	private boolean isFinished = false; // 데이터를 모두 읽었는지 체크하는 플래그
	private int retryLimit = 3; // 재시도 횟수 제한

	@Override
	public GemLogDto read() throws Exception {
		// 만약 에러로 인해 처리되지 않은 데이터가 있다면 다시 시도
		if (!unprocessedData.isEmpty()) {
			String key = unprocessedData.poll();
			fetchDataFromRedis(key); // 에러가 발생한 키에 대해서만 다시 시도
		}

		if (cached.isEmpty() && !isFinished) {
			fetchDataFromRedis(null); // 처음 데이터 가져오기
		}

		return cached.poll(); // null 반환 시 자동 종료
	}

	private void fetchDataFromRedis(String key) {
		Set<String> keys = redisTemplate.keys(RedisKey.GEM_LOG.getKey() + "*");
		if (key != null && !keys.contains(key)) {
			log.warn("Key {} not found in Redis", key);
			return; // 해당 키가 Redis에 없으면 바로 종료
		}

		for (String currentKey : keys) {
			List<String> logs = redisTemplate.opsForList().range(currentKey, 0, -1);

			for (String json : logs) {
				try {
					GemLogDto logDto = objectMapper.readValue(json, GemLogDto.class);
					cached.add(logDto);

					// 캐시 크기 제한에 도달하면 더 이상 데이터를 추가하지 않음
					if (cached.size() >= CACHE_LIMIT) {
						log.info("Cache limit reached, stopping data fetch.");
						return;
					}
				} catch (Exception e) {
					log.error("Failed to parse JSON for key {}: {}", currentKey, json, e);

					// 에러가 발생한 데이터는 unprocessedData에 추가하여 재시도 할 수 있도록 함
					unprocessedData.add(currentKey);
					if (unprocessedData.size() >= retryLimit) {
						log.warn("Exceeded retry limit for key {}. Will not retry further.", currentKey);
					}
				}
			}

			// 데이터 처리 후 해당 Redis 키 삭제
			redisTemplate.delete(currentKey);
		}

		if (keys.isEmpty()) {
			isFinished = true; // 더 이상 가져올 데이터가 없으면 종료 플래그 설정
		}

		log.info("Successfully fetched {} logs from Redis.", cached.size());
	}
}
