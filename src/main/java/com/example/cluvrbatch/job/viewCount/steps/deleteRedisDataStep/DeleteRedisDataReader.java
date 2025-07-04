package com.example.cluvrbatch.job.viewCount.steps.deleteRedisDataStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

public class DeleteRedisDataReader implements ItemReader<String>, StepExecutionListener {
	private List<String> keys;
	private Iterator<String> iterator;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext context = stepExecution.getJobExecution().getExecutionContext();
		Set<String> keySet = (Set<String>) context.get("viewCountKeys"); // ← 올바른 캐스팅
		this.keys = new ArrayList<>(keySet); // List로 변환
		this.iterator = keys.iterator();
	}

	@Override
	public String read() {
		return iterator.hasNext() ? iterator.next() : null;
	}
}
