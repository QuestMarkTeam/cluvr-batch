package com.example.cluvrbatch.job.reviewRequest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;
import com.example.cluvrbatch.job.reviewRequest.entity.TilReview;

public interface TilReviewRepository extends JpaRepository<TilReview, Long> {
	List<ReviewRequestDto> findAllByReviewedIsFalse();
}
