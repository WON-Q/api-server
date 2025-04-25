package com.fisa.wonq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WonqApplication {

	public static void main(String[] args) {
		SpringApplication.run(WonqApplication.class, args);
	}

}
