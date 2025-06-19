package com.example.cluvrbatch.job.useractivity.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.cluvrbatch.job.useractivity.dto.BoardStatEventResponseDto;

@RequiredArgsConstructor
@Repository
public class BoardStatJdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	public void batchInsert(List<? extends BoardStatEventResponseDto> items) {
		String sql = "INSERT INTO user_stat " +
			"(user_id, total_answer, total_selected, total_question, total_clover,tier) " +
			"VALUES (?, ?, ?, ?,?, ?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				BoardStatEventResponseDto dto = items.get(i);
				ps.setLong(1, dto.getUserId());
				ps.setInt(2, dto.getTotalAnswer());
				ps.setInt(3, dto.getTotalSelected());
				ps.setInt(4, dto.getTotalQuestion());
				ps.setInt(5, dto.getTotalClover());
				ps.setString(6, dto.getTier().name());
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}
}
