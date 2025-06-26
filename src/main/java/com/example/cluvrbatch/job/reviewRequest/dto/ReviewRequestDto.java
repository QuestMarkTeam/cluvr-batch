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
}
