package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.main"})
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = {"com.main.repository", "com.jktech.documentmanagement.repository"})
public class DocumentManager {

	public static void main(String[] args) {
		SpringApplication.run(DocumentManager.class, args);
	}

}
