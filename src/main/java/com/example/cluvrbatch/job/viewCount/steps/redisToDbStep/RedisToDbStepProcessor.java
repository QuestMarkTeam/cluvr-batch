package com.example.cluvrbatch.job.viewCount.steps.redisToDbStep;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;

@RequiredArgsConstructor
public class RedisToDbStepProcessor implements ItemProcessor<BoardViewCount, BoardViewCount> {

	private final RedisTemplate<String, Long> redisTemplate;

	@Override
	public BoardViewCount process(BoardViewCount item) {
		// 예: 조회수가 0 이상인 경우만 처리
		if (item.getViewCount() <= 0) {
			return null; // null 반환하면 writer에 전달되지 않음
		}

		return item;
	}
}
