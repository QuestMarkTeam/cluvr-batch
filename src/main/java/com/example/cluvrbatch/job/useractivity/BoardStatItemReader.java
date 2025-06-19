package com.example.cluvrbatch.job.useractivity;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.enums.RedisKey;
import com.example.cluvrbatch.job.useractivity.dto.BoardStatEventResponseDto;
import com.example.cluvrbatch.job.useractivity.enums.Tier;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class BoardStatItemReader implements ItemReader<BoardStatEventResponseDto> {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private Queue<BoardStatEventResponseDto> cached = new LinkedList<>();

	@Override
	public BoardStatEventResponseDto read() throws Exception {
		if (cached.isEmpty()) {
			Set<String> keys = redisTemplate.keys(RedisKey.BOARD_ACTIVITY_LOG.getKey() + "*");

			for (String key : keys) {
				Map<Object, Object> statMap = redisTemplate.opsForHash().entries(key);

				if (statMap.isEmpty())
					continue;

				Long userId = Long.parseLong(key.substring(key.lastIndexOf(":") + 1));
				String raw = (String)statMap.get(RedisKey.USER_TIER.getKey());
				String cleaned = raw.replace("\"", "");
				Tier tier = Tier.valueOf(cleaned);
				BoardStatEventResponseDto dto = BoardStatEventResponseDto.of(
					userId,
					Integer.parseInt((String)statMap.get(RedisKey.TOTAL_ANSWER.getKey())),
					Integer.parseInt((String)statMap.get(RedisKey.TOTAL_SELECTED.getKey())),
					Integer.parseInt((String)statMap.get(RedisKey.TOTAL_QUESTION.getKey())),
					Integer.parseInt((String)statMap.get(RedisKey.TOTAL_CLOVER.getKey())),
					tier
				);

				Tier updatedTier = Tier.checkAndUpgrade(
					dto.getTier().getRevel(),
					dto.getTotalClover(),
					dto.getTotalQuestion(),
					dto.getTotalAnswer()
				);
				dto.updateTier(updatedTier);

				cached.add(dto);
				redisTemplate.delete(key);
			}
		}

		return cached.poll();
	}
}
