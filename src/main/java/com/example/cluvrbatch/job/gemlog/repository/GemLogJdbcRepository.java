package com.example.cluvrbatch.job.gemlog.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.cluvrbatch.job.gemlog.dto.GemEventResponseDto;

@RequiredArgsConstructor
@Repository
public class GemLogJdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	public void batchInsert(List<? extends GemEventResponseDto> items) {
		String sql = "INSERT INTO gem_log (user_id, amount, description, created_at, deleted_at, flow_type, action) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				GemEventResponseDto dto = items.get(i);

				ps.setLong(1, dto.getUserId());
				ps.setInt(2, dto.getAmount());
				ps.setString(3, dto.getDescription());
				ps.setObject(4, dto.getCreatedAt());
				ps.setObject(5, dto.getDeletedAt());
				ps.setString(6, dto.getFlowType().name());
				ps.setString(7, dto.getAction());
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}
}
