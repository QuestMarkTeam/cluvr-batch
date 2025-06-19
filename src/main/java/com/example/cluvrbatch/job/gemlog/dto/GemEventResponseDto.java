package com.example.cluvrbatch.job.gemlog.dto;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrbatch.job.gemlog.enums.GemActionType;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class GemEventResponseDto {
	private Long userId;
	private Integer amount;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private GemActionType flowType;
	private String action;

	// 30일간 기준으로 최신 gem 로그를 보여주기위해 레디스에 저장
	public GemEventResponseDto(Long userId, Integer amount, String description, LocalDateTime createdAt,
		LocalDateTime deletedAt, GemActionType flowType, String action) {
		this.userId = userId;
		this.amount = amount;
		this.description = description;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.flowType = flowType;
		this.action = action;
	}

	public static GemEventResponseDto of(Long userId, Integer amount, String description, LocalDateTime createdAt,
		LocalDateTime deletedAt, GemActionType flowType, String action) {
		return new GemEventResponseDto(userId, amount, description, createdAt, deletedAt, flowType,
			action);
	}
}
