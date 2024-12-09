package com.assignment.flightAPI;

import com.assignment.flightAPI.config.LocalElasticsearchContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightApiApplicationIntegrationTest {

	@BeforeAll
	static void runBeforeSpringContextLoads() {
		LocalElasticsearchContainer.getElasticsearchUrl();
	}

	@LocalServerPort
	private int port;

	@Test
	void contextLoads() {
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testGetFlightsByFilters_NoFilters() {
		String url = "http://localhost:" + port + "/flights";
		ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().size()).isEqualTo(5000);
	}

	@Test
	void testGetFlightsByFilters_WithDestination() {
		String url = "http://localhost:" + port + "/flights?destination=JFK";
		ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().size()).isEqualTo(485);
	}

	@Test
	void testGetFlightsByFilters_WithInvalidDestination() {
		String url = "http://localhost:" + port + "/flights?destination=ASDF";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
		Assertions.assertThat(response.getBody()).isNotNull();
	}

	@Test
	void testGetFlightsByFilters_WithDateRange() {
		String url = "http://localhost:" + port + "/flights?startScheduleDateTime=2022-12-03&endScheduleDateTime=2024-12-13";
		ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().size()).isEqualTo(1223);
	}

	@Test
	void testGetFlightsByFilters_WithMultipleFilters() {
		// Call the endpoint with multiple filters
		String url = "http://localhost:" + port + "/flights?destination=JFK&flightDirection=DEPARTURE&status=ARRIVED&startScheduleDateTime=2022-12-03&endScheduleDateTime=2024-12-13";
		ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

		// Assert the response
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Assertions.assertThat(response.getBody()).isNotNull(); // Replace with specific checks for your response body
		Assertions.assertThat(response.getBody().size()).isEqualTo(10);
	}

}
