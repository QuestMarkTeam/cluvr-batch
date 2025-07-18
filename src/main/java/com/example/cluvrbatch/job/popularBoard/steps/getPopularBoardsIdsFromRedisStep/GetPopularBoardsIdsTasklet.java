package com.example.cluvrbatch.job.popularBoard.steps.getPopularBoardsIdsFromRedisStep;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@RequiredArgsConstructor
public class GetPopularBoardsIdsTasklet implements Tasklet {
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		// ZSet에서 상위 5개 board id 추출
		Set<String> boardIds = redisTemplate.opsForZSet()
			.reverseRange("popular:board", 0, 4);

		// String key = String.format(REDIS_KEY_FORMAT, categoryType.name().toLowerCase());
		//
		// Set<ZSetOperations.TypedTuple<String>> resultSet = redisTemplate.opsForZSet()
		// 	.reverseRangeWithScores(key, 0, 4); // 내림차순 상위 5개
		//
		// if (resultSet == null || resultSet.isEmpty()) {
		// 	return List.of();
		// }
		//
		// return resultSet.stream()
		// 	.map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
		// 	.map(Long::parseLong)
		// 	.collect(Collectors.toList());



		// StepExecutionContext에 저장 (다음 step에서 사용)
		chunkContext.getStepContext().getStepExecution()
			.getJobExecution().getExecutionContext()
			.put("popularBoardIds", boardIds);

		return RepeatStatus.FINISHED;
	}
}
