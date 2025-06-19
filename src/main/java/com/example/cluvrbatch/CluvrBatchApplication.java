package com.example.cluvrbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CluvrBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CluvrBatchApplication.class, args);
	}

}
