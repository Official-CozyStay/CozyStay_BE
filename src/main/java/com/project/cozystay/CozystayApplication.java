package com.project.cozystay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CozystayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CozystayApplication.class, args);
	}

}
