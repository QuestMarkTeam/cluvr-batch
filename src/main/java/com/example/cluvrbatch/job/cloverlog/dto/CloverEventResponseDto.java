package com.example.cluvrbatch.job.cloverlog.dto;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrbatch.job.cloverlog.enums.CloverActionType;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CloverEventResponseDto { // redis에 올릴 데이터

	private Long userId;
	private Integer amount;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private String description;
	private String action; // 어떤 활동으로 적립인지
	private CloverActionType flowType; // 사용인지 적립인지

	public CloverEventResponseDto(Integer amount, LocalDateTime createdAt, LocalDateTime deletedAt, String description,
		Long userId, String action, CloverActionType flowType) {
		this.amount = amount;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.description = description;
		this.userId = userId;
		this.action = action;
		this.flowType = flowType;
	}

	public static CloverEventResponseDto of(Integer amount, LocalDateTime createdAt, LocalDateTime deletedAt,
		String description, Long userId, String action, CloverActionType flowType) {
		return new CloverEventResponseDto(amount, createdAt, deletedAt, description, userId, action,
			flowType);
	}

}

