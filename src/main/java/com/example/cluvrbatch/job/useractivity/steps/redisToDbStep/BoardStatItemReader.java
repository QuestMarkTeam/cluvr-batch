package com.example.cluvrbatch.job.useractivity.steps.redisToDbStep;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.enums.RedisKey;
import com.example.cluvrbatch.job.useractivity.dto.BoardStatDto;
import com.example.cluvrbatch.job.useractivity.enums.Tier;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardStatItemReader implements ItemReader<BoardStatDto> {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Queue<BoardStatDto> cached = new LinkedList<>();
	private Queue<String> unprocessedData = new LinkedList<>(); // 에러 발생한 데이터를 추적할 큐
	private static final int CACHE_LIMIT = 1000; // 캐시 크기 제한 (최대 1000개 항목)
	private static final int BATCH_SIZE = 100; // 한 번에 가져올 데이터의 수 (성능 최적화)
	private boolean isFinished = false; // 데이터를 모두 읽었는지 체크하는 플래그
	private int retryLimit = 3; // 재시도 횟수 제한

	@Override
	public BoardStatDto read() throws Exception {
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
		// SCAN 옵션 설정 (대상 키 패턴)
		ScanOptions options = ScanOptions.scanOptions()
			.match(RedisKey.BOARD_ACTIVITY_LOG.getKey() + "*") // 특정 패턴에 맞는 키만 가져옴
			.count(BATCH_SIZE) // 한 번에 가져올 데이터의 개수 (배치 크기)
			.build();

		try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
			boolean hasMoreData = true; // SCAN이 완료되지 않았는지 추적

			while (cursor.hasNext() && hasMoreData) {
				String currentKey = new String(cursor.next()); // 키를 가져옴
				Map<Object, Object> statMap = redisTemplate.opsForHash().entries(currentKey); // 해당 키의 해시 값

				if (statMap.isEmpty()) {
					continue;
				}

				try {
					Long userId = Long.parseLong(currentKey.substring(currentKey.lastIndexOf(":") + 1)); // userId 추출
					String raw = (String) statMap.get(RedisKey.USER_TIER.getKey());
					String cleaned = raw.replace("\"", "");
					Tier tier = Tier.valueOf(cleaned); // Tier 값 설정

					// BoardStatDto 객체 생성
					BoardStatDto dto = BoardStatDto.of(
						userId,
						parseIntSafely(statMap.get(RedisKey.TOTAL_ANSWER.getKey())),
						parseIntSafely(statMap.get(RedisKey.TOTAL_SELECTED.getKey())),
						parseIntSafely(statMap.get(RedisKey.TOTAL_QUESTION.getKey())),
						parseIntSafely(statMap.get(RedisKey.TOTAL_CLOVER.getKey())),
						tier
					);

					// 캐시에 저장
					cached.add(dto);

					// 캐시 크기 제한에 도달하면 더 이상 데이터를 추가하지 않음
					if (cached.size() >= CACHE_LIMIT) {
						log.info("Cache limit reached, stopping data fetch.");
						hasMoreData = false;
						break;
					}

					// 처리한 후 해당 Redis 키 삭제
					redisTemplate.delete(currentKey);

				} catch (Exception e) {
					log.error("Failed to parse Redis entry for key {}: {}", currentKey, e.getMessage());

					// 에러가 발생한 데이터는 unprocessedData에 추가하여 재시도 할 수 있도록 함
					unprocessedData.add(currentKey);
					if (unprocessedData.size() >= retryLimit) {
						log.warn("Exceeded retry limit for key {}. Will not retry further.", currentKey);
					}
				}
			}

			// SCAN이 종료되면 더 이상 데이터를 가져오지 않음
			if (!cursor.hasNext()) {
				isFinished = true; // 더 이상 가져올 데이터가 없으면 종료 플래그 설정
			}

			log.info("Successfully fetched {} logs from Redis.", cached.size());

		} catch (Exception e) {
			log.error("Error fetching data from Redis", e);
		}
	}

	// 안전한 Integer 변환
	private Integer parseIntSafely(Object value) {
		try {
			return value != null ? Integer.parseInt(value.toString()) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
