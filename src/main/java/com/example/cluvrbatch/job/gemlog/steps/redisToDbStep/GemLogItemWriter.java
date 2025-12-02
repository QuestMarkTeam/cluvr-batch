package com.example.cluvrbatch.job.gemlog.steps.redisToDbStep;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.gemlog.dto.GemLogDto;
import com.example.cluvrbatch.job.gemlog.repository.GemLogJdbcRepository;

@Component
@RequiredArgsConstructor
public class GemLogItemWriter implements ItemWriter<GemLogDto> {

	private final GemLogJdbcRepository gemLogJdbcRepository;

	@Override
	public void write(Chunk<? extends GemLogDto> chunk) {
		gemLogJdbcRepository.batchInsert(chunk.getItems());
	}
}
