package com.example.cluvrbatch.job.useractivity.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrbatch.job.useractivity.enums.Tier;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class BoardStatEventResponseDto { // redis에 올릴 데이터

	private Long userId;
	private Integer totalAnswer;
	private Integer totalClover;
	private Integer totalSelected;
	private Integer totalQuestion;
	private Tier tier;

	public BoardStatEventResponseDto(Long userId, Integer totalAnswer, Integer totalClover, Integer totalSelected,
		Integer totalQuestion, Tier tier) {
		this.userId = userId;
		this.totalAnswer = totalAnswer;
		this.totalClover = totalClover;
		this.totalSelected = totalSelected;
		this.totalQuestion = totalQuestion;
		this.tier = tier;
	}

	public static BoardStatEventResponseDto of(Long userId, Integer totalAnswer, Integer totalClover,
		Integer totalSelected,
		Integer totalQuestion, Tier tier) {
		return new BoardStatEventResponseDto(userId, totalAnswer, totalClover, totalSelected, totalQuestion, tier);
	}

	public void updateTier(Tier tier) {
		this.tier = tier;
	}
}

