package com.example.cluvrbatch.job.viewCount.steps.redisToDbStep;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;
import com.example.cluvrbatch.job.viewCount.repository.BoardJdbcRepository;

@RequiredArgsConstructor
public class ViewCountWriter implements ItemWriter<BoardViewCount> {
	private final BoardJdbcRepository boardJdbcRepository;

	@Override
	public void write(Chunk<? extends BoardViewCount> chunk) {
		boardJdbcRepository.batchUpdate(chunk.getItems());
	}
}
