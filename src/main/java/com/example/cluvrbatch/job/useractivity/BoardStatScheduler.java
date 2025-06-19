package com.example.cluvrbatch.job.useractivity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoardStatScheduler {

	private final JobLauncher jobLauncher;
	private final Job boardLogJob;
	private final BoardStatJobService boardStatJobService;

	@Scheduled(cron = "0 * * * * *") // 매일 새벽 3시
	public void runBoardStatJob() {
		try {
			log.info("batch 스캐쥴 잘 실행 중 ㅋㅋㅋ");
			boardStatJobService.runJob();
		} catch (Exception e) {
			// 로깅 or 슬랙 알림
			throw new IllegalStateException("runBoardStatJob 실행 실패", e);
		}
	}
}
