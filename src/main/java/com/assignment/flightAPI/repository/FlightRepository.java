package com.assignment.flightAPI.repository;

import com.assignment.flightAPI.model.Flight;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FlightRepository extends ElasticsearchRepository<Flight, String> {
}
