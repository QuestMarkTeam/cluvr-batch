package com.example.cluvrbatch.job.reviewRequest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "til_reviews")
public class TilReview {
	@Id
	private Long id;

	private String tilContent;

	private boolean reviewed;

	private String feedback;

	private String summary;

	private int score;

	public void updateReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public void updateFeedback(String feedback) {
		this.feedback = feedback;
	}

	public void updateSummary(String summary) {
		this.summary = summary;
	}

	public void updateScore(int score) {
		this.score = score;
	}
}

