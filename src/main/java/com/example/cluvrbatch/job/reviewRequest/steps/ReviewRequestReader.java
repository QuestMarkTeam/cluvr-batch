package com.example.cluvrbatch.job.reviewRequest.steps;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.reviewRequest.dto.ReviewRequestDto;

@Component
@RequiredArgsConstructor
public class ReviewRequestReader implements ItemReader<ReviewRequestDto> {

	private final JdbcTemplate jdbcTemplate;

	private Queue<ReviewRequestDto> cached = new LinkedList<>();

	@Override
	public ReviewRequestDto read() throws Exception {
		if (cached.isEmpty()) {
			// 아직 데이터가 없으면 DB에서 한 번에 읽어서 캐싱
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
		}

		// 큐에서 하나씩 반환, null 반환 시 종료 신호
		return cached.poll();
	}
}
