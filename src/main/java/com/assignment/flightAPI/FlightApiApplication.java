package com.assignment.flightAPI;

import com.assignment.flightAPI.config.LocalElasticsearchContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlightApiApplication {

	private static final Logger logger = LoggerFactory.getLogger(FlightApiApplication.class);

	public static void main(String[] args) {
		logger.info("For elastic search url is- {}", LocalElasticsearchContainer.getElasticsearchUrl());
		SpringApplication.run(FlightApiApplication.class, args);
	}

}
