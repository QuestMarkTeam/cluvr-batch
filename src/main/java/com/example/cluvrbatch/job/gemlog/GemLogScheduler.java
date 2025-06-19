package com.example.cluvrbatch.job.gemlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GemLogScheduler {

	private final JobLauncher jobLauncher;
	private final GemJobService gemJobService;
	private final Job gemLogJob;

	@Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
	public void runGemLogJob() {
		try {
			gemJobService.runJob();
		} catch (Exception e) {
			// 로깅 or 슬랙 알림
			throw new IllegalStateException("runGemLogJob 실행 실패", e);

		}
	}
}
