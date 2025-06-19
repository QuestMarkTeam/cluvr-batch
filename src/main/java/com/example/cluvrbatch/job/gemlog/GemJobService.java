package com.example.cluvrbatch.job.gemlog;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GemJobService {
	private final JobLauncher jobLauncher;
	private final Job gemLogJob;

	public void runJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(gemLogJob, jobParameters);
	}
}
