package com.example.cluvrbatch.job.launcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class Scheduler {
	private final JobLauncher jobLauncher;
	private final Job viewCountJob;
	private final Job boardLogJob;
	private final Job gemLogJob;
	private final Job cloverLogJob;

	@Scheduled(cron = "0 0 3 * * *") // 매일 3시
	public void runViewCountJob() throws Exception {
		try {
			log.info("▶ [START] viewCountJob");
			jobLauncher.run(viewCountJob, params());
			log.info("✅ [SUCCESS] viewCountJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] viewCountJob", e);
			throw e;
		}
	}

	@Scheduled(cron = "0 5 3 * * *") // 매일 3시 5분
	public void runBoardStatJob() throws Exception {
		try {
			log.info("▶ [START] boardLogJob");
			jobLauncher.run(boardLogJob, params());
			log.info("✅ [SUCCESS] boardLogJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] boardLogJob", e);
			throw e;
		}
	}

	@Scheduled(cron = "0 10 3 * * *") // 매일 3시 10분
	public void runGemLogJob() throws Exception {
		try {
			log.info("▶ [START] gemLogJob");
			jobLauncher.run(gemLogJob, params());
			log.info("✅ [SUCCESS] gemLogJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] gemLogJob", e);
			throw e;
		}
	}

	@Scheduled(cron = "0 15 3 * * *") // 매일 3시 15분
	public void runCloverLogJob() throws Exception {
		try {
			log.info("▶ [START] cloverLogJob");
			jobLauncher.run(cloverLogJob, params());
			log.info("✅ [SUCCESS] cloverLogJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] cloverLogJob", e);
			throw e;
		}
	}

	private JobParameters params() {
		return new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
	}
}
