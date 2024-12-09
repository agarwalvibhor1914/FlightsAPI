package com.assignment.flightAPI.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.assignment.flightAPI.model.Flight;
import com.assignment.flightAPI.model.FlightDirection;
import com.assignment.flightAPI.model.Status;
import com.assignment.flightAPI.services.FlightsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FlightsServiceTest {

    @InjectMocks
    private FlightsService flightsService;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFlightsByFilters_WithValidFilters() throws IOException {
        String destination = "JFK";
        FlightDirection direction = FlightDirection.ARRIVAL;
        Status status = Status.SCHEDULED;
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";

        SearchResponse<Flight> mockResponse = mock(SearchResponse.class);
        Flight flight1 = generateFlight("TEST1", "JFK", LocalDateTime.now().minusDays(5), FlightDirection.ARRIVAL, Status.SCHEDULED);
        Flight flight2 = generateFlight("TEST2", "JFK", LocalDateTime.now().minusDays(3), FlightDirection.ARRIVAL, Status.SCHEDULED);
        Hit<Flight> hit1 = Hit.of(h -> h
                .source(flight1)
                .index("flight-index")
                .id("1"));
        Hit<Flight> hit2 = Hit.of(h -> h
                .source(flight2)
                .index("flight-index")
                .id("2"));
        HitsMetadata<Flight> mockHitsMetadata = mock(HitsMetadata.class);
        when(mockHitsMetadata.hits()).thenReturn(List.of(hit1,hit2));
        when(mockResponse.hits()).thenReturn(mockHitsMetadata);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Flight.class))).thenReturn(mockResponse);

        List<Flight> flights = flightsService.getFlightsByFilters(destination, direction, status, startDate, endDate);

        assertEquals(2, flights.size());
        assertEquals("JFK", flights.get(0).getDestination());
        assertEquals(FlightDirection.ARRIVAL, flights.get(0).getFlightDirection());
        assertEquals(LocalDateTime.now().minusDays(5).toLocalDate(), flights.get(0).getScheduleDateTime().toLocalDate());
        verify(elasticsearchClient, times(1)).search(any(SearchRequest.class), eq(Flight.class));
    }

    @Test
    void testGetFlightsByFilters_WithNoFilters() throws IOException {
        String destination = null;
        FlightDirection direction = null;
        Status status = null;
        String startDate = null;
        String endDate = null;

        Flight flight1 = generateFlight("TEST1","AMS", LocalDateTime.now().minusDays(10), FlightDirection.DEPARTURE, Status.DEPARTED);
        Flight flight2 = generateFlight("TEST2", "JFK", LocalDateTime.now().minusDays(3), FlightDirection.ARRIVAL, Status.SCHEDULED);
        Hit<Flight> hit1 = Hit.of(h -> h
                .source(flight1)
                .index("flight-index")
                .id("1"));
        Hit<Flight> hit2 = Hit.of(h -> h
                .source(flight2)
                .index("flight-index")
                .id("2"));

        SearchResponse<Flight> mockResponse = mock(SearchResponse.class);
        HitsMetadata<Flight> mockHitsMetadata = mock(HitsMetadata.class);
        when(mockHitsMetadata.hits()).thenReturn(List.of(hit1,hit2));
        when(mockResponse.hits()).thenReturn(mockHitsMetadata);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Flight.class))).thenReturn(mockResponse);

        List<Flight> flights = flightsService.getFlightsByFilters(destination, direction, status, startDate, endDate);

        assertEquals(2, flights.size());
        assertEquals("AMS", flights.get(0).getDestination());
        assertEquals(FlightDirection.DEPARTURE, flights.get(0).getFlightDirection());
        assertEquals(LocalDateTime.now().minusDays(10).toLocalDate(), flights.get(0).getScheduleDateTime().toLocalDate());
        verify(elasticsearchClient, times(1)).search(any(SearchRequest.class), eq(Flight.class));
    }


    private Flight generateFlight(String id,String destination, LocalDateTime scheduleDateTime,
                                  FlightDirection flightDirection, Status status){
        Flight flight = new Flight();
        flight.setId(id);
        flight.setDestination(destination);
        flight.setScheduleDateTime(scheduleDateTime);
        flight.setFlightDirection(flightDirection);
        flight.setStatus(status);

        return flight;
    }
}
