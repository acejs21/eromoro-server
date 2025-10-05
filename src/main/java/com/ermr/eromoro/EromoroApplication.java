package com.ermr.eromoro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class EromoroApplication {

	public static void main(String[] args) {
		SpringApplication.run(EromoroApplication.class, args);
	}

}
