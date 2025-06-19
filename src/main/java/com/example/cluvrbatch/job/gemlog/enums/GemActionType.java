package com.example.cluvrbatch.job.gemlog.enums;

import java.time.LocalDateTime;

public enum GemActionType {
	EARN(1),   // 적립
	USE(-1),  // 사용
	EXPIRE(-1), // 소멸
	REFUND(-1) // 환불
	;

	private final Integer multiplier;

	GemActionType(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public Integer apply(Integer amount) {
		return amount * multiplier;
	}

	public LocalDateTime getEventDate() {
		return this == EARN ? LocalDateTime.now() : null;
	}

	public LocalDateTime getDeleteDate() {
		return this == USE || this == EXPIRE || this == REFUND ? LocalDateTime.now() : null;
	}
}
