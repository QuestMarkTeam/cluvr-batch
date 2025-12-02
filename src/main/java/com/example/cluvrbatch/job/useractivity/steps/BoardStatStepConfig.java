package com.example.cluvrbatch.job.useractivity.steps;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.cluvrbatch.job.enums.JobStepName;
import com.example.cluvrbatch.job.useractivity.dto.BoardStatDto;
import com.example.cluvrbatch.job.useractivity.repository.BoardStatJdbcRepository;
import com.example.cluvrbatch.job.useractivity.steps.redisToDbStep.BoardStatItemReader;
import com.example.cluvrbatch.job.useractivity.steps.redisToDbStep.BoardStatItemWriter;

@Configuration
@RequiredArgsConstructor
public class BoardStatStepConfig {

	private final RedisTemplate<String, String> redisTemplate;
	private final BoardStatJdbcRepository boardStatJdbcRepository;


	@Bean
	public Step boardEventStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
		ItemReader<BoardStatDto> boardLogReader,
		ItemWriter<BoardStatDto> boardLogWriter) { // Job을 구성하는 단일 Step 단계 정의 Step = 그 안에서 실행되는 한 단계 (ex. Redis → RDS 저장)
		return new StepBuilder(JobStepName.BOARD_STAT_JOB.name(), jobRepository)
			.<BoardStatDto, BoardStatDto>chunk(1000, transactionManager)
			.reader(boardLogReader)
			.writer(boardLogWriter)
			.build();
	}

	@Bean
	public ItemReader<BoardStatDto> boardLogReader() {
		return new BoardStatItemReader(redisTemplate);
	}

	@Bean
	public ItemWriter<BoardStatDto> boardLogWriter() {
		return new BoardStatItemWriter(boardStatJdbcRepository);
	}
}
