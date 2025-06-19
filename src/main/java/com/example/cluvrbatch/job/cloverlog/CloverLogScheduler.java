package com.example.cluvrbatch.job.cloverlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloverLogScheduler {

	private final JobLauncher jobLauncher;
	private final Job cloverLogJob;
	private final CloverJobService cloverJobService;

	@Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
	public void runCloverLogJob() {
		try {
			cloverJobService.runJob();
		} catch (Exception e) {
			// 로깅 or 슬랙 알림
			throw new IllegalStateException("runCloverLogJob 실행 실패", e);

		}
	}
}
