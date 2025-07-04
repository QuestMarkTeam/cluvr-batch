package com.example.cluvrbatch.job.viewCount.steps.redisToDbStep;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;

@Slf4j
@RequiredArgsConstructor
public class RedisToDbStepReader implements ItemReader<BoardViewCount>, StepExecutionListener {

	private final StringRedisTemplate redisTemplate;
	private final RedisTemplate<String, Long> redisLongTemplate;
	private StepExecution stepExecution;
	private Queue<BoardViewCount> cache = new LinkedList<>();
	private static final String REDIS_KEY_PATTERN = "board:*:views";
	// board:*:views
	//hash map
	//


	@Override
	public void beforeStep(StepExecution stepExecution) {
		Set<String> keys = redisTemplate.keys(REDIS_KEY_PATTERN);

		stepExecution.getJobExecution().getExecutionContext().put("viewCountKeys", keys);

		for (String key : keys) {
			Long viewCount = redisLongTemplate.opsForValue().get(key);

			if (viewCount != null) {
				try {
					Long boardId = extractBoardIdFromKey(key);
					cache.add(new BoardViewCount(boardId, viewCount));
				} catch (NumberFormatException e) {
					log.warn("Invalid value or key: {} -> {}", key, viewCount);
				}
			}
		}
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.EXECUTING;
	}

	@Override
	public BoardViewCount read() {
		System.out.println(cache.size() + " cache size");
		return cache.poll();
	}

	private Long extractBoardIdFromKey(String key) {
		// 예: "board:9:views" -> 9
		String[] parts = key.split(":");
		if (parts.length != 3 || !"board".equals(parts[0]) || !"views".equals(parts[2])) {
			throw new IllegalArgumentException("Invalid key format: " + key);
		}

		return Long.valueOf(parts[1]);
	}
}
