package com.example.cluvrbatch.openai.dto;

import lombok.Getter;

@Getter
public class ReviewResultDto {
	private String feedback;
	private String summary;
	private int score;

	public ReviewResultDto(String feedback, String summary, int score) {
		this.feedback = feedback;
		this.summary = summary;
		this.score = score;
	}

	public static ReviewResultDto parseReviewResult(String aiResponse) {
		if (aiResponse == null || !aiResponse.contains("|")) {
			throw new IllegalArgumentException("AI 응답 형식 오류: " + aiResponse);
		}

		String[] parts = aiResponse.split("\\|");
		if (parts.length != 3) {
			throw new IllegalArgumentException("AI 응답 파싱 실패 (3개로 분리 불가): " + aiResponse);
		}

		String feedback = parts[0].trim();
		String summary = parts[1].trim();
		int score = Integer.parseInt(parts[2].trim());

		return new ReviewResultDto(feedback, summary, score);
	}
}
