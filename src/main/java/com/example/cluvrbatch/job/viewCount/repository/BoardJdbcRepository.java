package com.example.cluvrbatch.job.viewCount.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.cluvrbatch.job.viewCount.dto.BoardViewCount;

@RequiredArgsConstructor
@Repository
public class BoardJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	public void batchUpdate(List<? extends BoardViewCount> items) {
		String sql = "UPDATE boards SET view_count = ? WHERE id = ?";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				BoardViewCount item = items.get(i);
				ps.setLong(1, item.getViewCount());
				ps.setLong(2, item.getId());
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}
}
