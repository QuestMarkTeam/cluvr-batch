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
	private final Job reviewRequestJob;
	private final Job reactionCountJob;

	@Scheduled(cron = "0 5 2 * * *") //오전 2시 5분에 실행
	public void runReactionCountJob() throws Exception {
		try {
			log.info("▶ [START] reactionCountJob");
			jobLauncher.run(reactionCountJob, params());
			log.info("✅ [SUCCESS] reactionCountJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] reactionCountJob", e);
			throw e;
		}
	}

	@Scheduled(cron = "0 0 3 * * *") //오전 3시에 실행
	// @Scheduled(fixedRate = 600000) //10분에 한번씩
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

	@Scheduled(cron = "0 0 4 * * MON")
	public void runReviewRequestJob() throws Exception {
		try {
			log.info("▶ [START] reviewRequestJob");
			jobLauncher.run(reviewRequestJob, params());
			log.info("✅ [SUCCESS] reviewRequestJob");
		} catch (Exception e) {
			log.error("❌ [FAIL] reviewRequestJob", e);
			throw e;
		}
	}

	private JobParameters params() {
		return new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
	}
}
