package com.example.cluvrbatch.job.popularBoard.steps.updatePopularBoardRedisDataStep;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.cluvrbatch.job.popularBoard.dto.ReadAllBoardsResponseDto;
import com.example.cluvrbatch.job.popularBoard.repository.BoardJdbcRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class UpdatePopularBoardTasklet implements Tasklet {

	private final BoardJdbcRepository boardRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper; // Jackson 등

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// 이전 step에서 저장한 boardIds 가져오기
		@SuppressWarnings("unchecked")
		Set<String> boardIds = (Set<String>) chunkContext.getStepContext().getStepExecution()
			.getJobExecution().getExecutionContext().get("popularBoardIds");

		if (boardIds == null || boardIds.isEmpty()) {
			return RepeatStatus.FINISHED;
		}

		// DB에서 board 데이터 조회
		List<ReadAllBoardsResponseDto> boards = boardRepository.findBoardsByIds(
			boardIds.stream().map(Long::valueOf).toList()
		);

		// 직렬화 후 Redis에 저장
		String key = "popularBoardData";
		String json = objectMapper.writeValueAsString(boards);
		redisTemplate.opsForValue().set(key, json);

		return RepeatStatus.FINISHED;
	}
}
