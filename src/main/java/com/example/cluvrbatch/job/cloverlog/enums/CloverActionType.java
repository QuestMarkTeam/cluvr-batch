package com.example.cluvrbatch.job.cloverlog.enums;

public enum CloverActionType { // 어떤 action 인지 정의
	EARN(1), // 적립
	USE(-1) // 사용
	;

	private final Integer multiplier;

	CloverActionType(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public Integer apply(Integer amount) {
		return amount * multiplier;
	}

}
