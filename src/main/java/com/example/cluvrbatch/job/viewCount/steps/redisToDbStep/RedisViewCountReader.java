package com.example.cluvrbatch.job.viewCount.steps.redisToDbStep;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;

@RequiredArgsConstructor
public class RedisViewCountReader implements ItemReader<BoardViewCount> {

	private final RedisTemplate<String, Long> redisTemplate;
	private Queue<BoardViewCount> cache = new LinkedList<>();
	private static final String REDIS_KEY_PATTERN = "board:*:views";

	@Override
	public BoardViewCount read() {
		if (cache.isEmpty()) {
			Set<String> keys = redisTemplate.keys(REDIS_KEY_PATTERN);

			if (keys == null || keys.isEmpty()) {
				return null;
			}

			for (String key : keys) {
				Long viewCount = redisTemplate.opsForValue().get(key);
				if (viewCount != null) {
					try {
						Long boardId = extractBoardIdFromKey(key);
						cache.add(new BoardViewCount(boardId, viewCount));
					} catch (NumberFormatException e) {
						System.err.println("Invalid value or key: " + key + " -> " + viewCount);
					}
				}
			}
		}

		System.out.println("되고 있나?" + cache.peek());

		return cache.poll();
	}

	private Long extractBoardIdFromKey(String key) {
		// 예: "board:9:views" -> 9
		String[] parts = key.split(":");
		return Long.valueOf(parts[1]);
	}
}
