package com.assignment.flightAPI.repository;

import com.assignment.flightAPI.model.Flight;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class FlightDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(FlightDataLoader.class);

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ObjectMapper mapper;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            logger.debug("Going to load records into Elasticsearch.");
            InputStream inputStream = new ClassPathResource("flight_data.json").getInputStream();
            List<Flight> flights = mapper.readValue(inputStream, new TypeReference<List<Flight>>() {
            });
            flightRepository.saveAll(flights);
            logger.info("Loaded 5000 flight records into Elasticsearch");
        };
    }
}
