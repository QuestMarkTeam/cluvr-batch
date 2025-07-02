package com.example.cluvrbatch.job.reviewRequest.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
	private Long id;
	private String tilContent;
	private Boolean reviewed;
	private int score;
	private String summary;
	private String feedback;

	public ReviewRequestDto(Long id, String tilContent, Boolean reviewed, int score, String summary, String feedback) {
		this.id = id;
		this.tilContent = tilContent;
		this.reviewed = reviewed;
		this.score = score;
		this.summary = summary;
		this.feedback = feedback;
	}
}
