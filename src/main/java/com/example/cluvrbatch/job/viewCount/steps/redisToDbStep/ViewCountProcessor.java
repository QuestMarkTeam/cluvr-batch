package com.example.cluvrbatch.job.viewCount.steps.redisToDbStep;

import org.springframework.batch.item.ItemProcessor;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;

public class ViewCountProcessor implements ItemProcessor<BoardViewCount, BoardViewCount> {
	@Override
	public BoardViewCount process(BoardViewCount item) {
		// 예: 조회수가 0 이상인 경우만 처리
		if (item.getViewCount() <= 0) {
			return null; // null 반환하면 writer에 전달되지 않음
		}

		return item;
	}
}
