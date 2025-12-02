package com.example.cluvrbatch.job.useractivity.steps.redisToDbStep;

import org.springframework.batch.item.ItemProcessor;

import com.example.cluvrbatch.job.useractivity.dto.BoardStatDto;
import com.example.cluvrbatch.job.useractivity.enums.Tier;

public class BoardStatProcessor implements ItemProcessor<BoardStatDto, BoardStatDto> {

	@Override
	public BoardStatDto process(BoardStatDto item) throws Exception {
		// Tier 업데이트
		Tier updatedTier = Tier.checkAndUpgrade(
			item.getTier().getRevel(),
			item.getTotalClover(),
			item.getTotalQuestion(),
			item.getTotalAnswer()
		);
		item.updateTier(updatedTier);

		return item;
	}
}
