package com.example.cluvrbatch.job.cloverlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.cloverlog.dto.CloverEventResponseDto;
import com.example.cluvrbatch.job.cloverlog.repository.CloverLogJdbcRepository;

@Component
@RequiredArgsConstructor
public class CloverLogItemWriter implements ItemWriter<CloverEventResponseDto> {

	private final CloverLogJdbcRepository cloverLogJdbcRepository;

	@Override
	public void write(Chunk<? extends CloverEventResponseDto> chunk) {
		cloverLogJdbcRepository.batchInsert(chunk.getItems());
	}
}
