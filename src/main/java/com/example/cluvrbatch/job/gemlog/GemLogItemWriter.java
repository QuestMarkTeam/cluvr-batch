package com.example.cluvrbatch.job.gemlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.gemlog.dto.GemEventResponseDto;
import com.example.cluvrbatch.job.gemlog.repository.GemLogJdbcRepository;

@Component
@RequiredArgsConstructor
public class GemLogItemWriter implements ItemWriter<GemEventResponseDto> {

	private final GemLogJdbcRepository gemLogJdbcRepository;

	@Override
	public void write(Chunk<? extends GemEventResponseDto> chunk) {
		gemLogJdbcRepository.batchInsert(chunk.getItems());
	}
}
