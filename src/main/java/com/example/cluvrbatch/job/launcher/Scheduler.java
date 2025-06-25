package com.example.cluvrbatch.job.launcher;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Scheduler {
	private final JobLauncher jobLauncher;
	private final Job viewCountJob;
	private final Job boardLogJob;
	private final Job gemLogJob;
	private final Job cloverLogJob;


	@Scheduled(cron = "0 0 3 * * *") // 매일 3시에
	// @Scheduled(initialDelay = 3000, fixedDelay = Long.MAX_VALUE) // 테스트를 위해 10초 후에 진행되게 만듬
	public void runRedisToDbJob() throws Exception {
		jobLauncher.run(viewCountJob, params());
	}

	@Scheduled(cron = "0 * * * * *") // 매일 새벽 3시
	public void runBoardStatJob() throws Exception {
		jobLauncher.run(boardLogJob, params());
	}

	@Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
	public void runGemLogJob() throws Exception {
		jobLauncher.run(gemLogJob, params());
	}

	@Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
	public void runCloverLogJob() throws Exception {
		jobLauncher.run(cloverLogJob, params());
	}

	private JobParameters params() {
		return new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
	}
}
