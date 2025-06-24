package com.example.cluvrbatch.job.useractivity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoardStatScheduler {

	private final JobLauncher jobLauncher;
	private final Job boardLogJob;
	private final BoardStatJobService boardStatJobService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
	public void runBoardStatJob() {
		try {
			log.info("batch 스캐쥴 잘 실행 중 0625 제발 지옥같은 여기서 날 꺼내줘 수정");

			// Redis 테스트 저장
			stringRedisTemplate.opsForValue().set("batch:ping", "pong");

			boardStatJobService.runJob();
		} catch (Exception e) {

			log.info("runBoardStatJob 실행 실패 이게 꿈이라면 어서 날 깨워줘 모든 것이 거짓말이라고 해줘", e);

		}
	}


}
