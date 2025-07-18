package com.example.cluvrbatch.job.popularBoard.repository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.cluvrbatch.job.popularBoard.dto.ReadAllBoardsResponseDto;

@RequiredArgsConstructor
@Repository("popularBoard")
public class BoardJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	public List<ReadAllBoardsResponseDto> findBoardsByIds(List<Long> boardIds) {
		if (boardIds == null || boardIds.isEmpty()) {
			return List.of();
		}

		String inSql = boardIds.stream().map(id -> "?").collect(Collectors.joining(","));
		String sql = "SELECT b.id, b.title, b.content, u.name as userName, b.created_at, b.modified_at " +
				"FROM boards b " +
				"JOIN users u ON b.user_id = u.id " +
				"WHERE b.id IN (" + inSql + ")";
		return jdbcTemplate.query(
			sql,
			boardIds.toArray(),
			(rs, rowNum) -> new ReadAllBoardsResponseDto(
				rs.getLong("id"),
				rs.getString("title"),
				rs.getString("content"),
				0L, // viewCount는 사용하지 않으므로 0으로 고정
				rs.getString("userName"),
				rs.getTimestamp("created_at").toLocalDateTime(),
				rs.getTimestamp("modified_at").toLocalDateTime()
			)
		);
	}
}
