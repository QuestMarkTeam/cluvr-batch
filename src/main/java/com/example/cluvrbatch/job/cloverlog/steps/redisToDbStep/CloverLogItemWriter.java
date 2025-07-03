package com.example.cluvrbatch.job.cloverlog.steps.redisToDbStep;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.cloverlog.dto.CloverLogDto;
import com.example.cluvrbatch.job.cloverlog.repository.CloverLogJdbcRepository;

@Component
@RequiredArgsConstructor
public class CloverLogItemWriter implements ItemWriter<CloverLogDto> {

	private final CloverLogJdbcRepository cloverLogJdbcRepository;

	@Override
	public void write(Chunk<? extends CloverLogDto> chunk) {
		cloverLogJdbcRepository.batchInsert(chunk.getItems());
	}
}
