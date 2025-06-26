package com.example.cluvrbatch.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Component;

import com.example.cluvrbatch.job.enums.JobStepName;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

@Component
public class CustomBatchMetrics implements MeterBinder {

	private final JobExplorer jobExplorer;

	public CustomBatchMetrics(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		for (JobExecution jobExecution : jobExplorer.findRunningJobExecutions(JobStepName.REVIEW_REQUEST_JOB.name())) {
			for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
				registry.gauge("custom.batch.step.read.count", stepExecution, StepExecution::getReadCount);
				registry.gauge("custom.batch.step.write.count", stepExecution, StepExecution::getWriteCount);
				registry.gauge("custom.batch.step.commit.count", stepExecution, StepExecution::getCommitCount);
			}
		}
	}
}

