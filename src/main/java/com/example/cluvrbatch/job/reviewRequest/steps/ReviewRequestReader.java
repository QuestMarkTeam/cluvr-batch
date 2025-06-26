package com.example.cluvrbatch.job.reviewRequest.steps;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewRequestReader implements ItemReader<ReviewRequestDto> {

	private final JdbcTemplate jdbcTemplate;

	private Queue<ReviewRequestDto> cached = new LinkedList<>();
	private boolean dataLoaded = false;

	@Override
	public ReviewRequestDto read() throws Exception {
		if (cached.isEmpty() && !dataLoaded) {
			try {
				List<ReviewRequestDto> list = jdbcTemplate.query(
					"SELECT id, til_content, reviewed, feedback, summary, score FROM til_reviews WHERE reviewed = false",
					(rs, rowNum) -> {
						ReviewRequestDto req = new ReviewRequestDto();
						req.setId(rs.getLong("id"));
						req.setTilContent(rs.getString("til_content"));
						req.setReviewed(rs.getBoolean("reviewed"));
						req.setFeedback(rs.getString("feedback"));
						req.setSummary(rs.getString("summary"));
						req.setScore(rs.getInt("score"));
						return req;
					}
				);
				cached.addAll(list);
			} catch (Exception e) {
				log.error("리뷰 요청 데이터 조회 실패", e);
				throw new RuntimeException("데이터베이스 조회 실패", e);
			}
		}

		return cached.poll();
	}
}
