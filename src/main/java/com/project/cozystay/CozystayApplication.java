package com.project.cozystay;

import com.project.cozystay.payment.config.TossPaymentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties(TossPaymentProperties.class)
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CozystayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CozystayApplication.class, args);
	}

}
