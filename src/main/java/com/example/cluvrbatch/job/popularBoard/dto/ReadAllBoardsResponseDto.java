package com.example.cluvrbatch.job.popularBoard.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // Jackson(objectMapper)은 역직렬 시 기본생성자가 리수
@AllArgsConstructor
public class ReadAllBoardsResponseDto {
	private long id;
	private String title;
	private String content;
	private long viewCount;
	private String userName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
