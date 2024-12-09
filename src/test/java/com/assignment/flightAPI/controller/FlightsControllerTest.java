package com.assignment.flightAPI.controller;

import com.assignment.flightAPI.model.Flight;
import com.assignment.flightAPI.model.FlightDirection;
import com.assignment.flightAPI.model.Status;
import com.assignment.flightAPI.services.FlightsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightsController.class)
public class FlightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightsService flightsService;

    @InjectMocks
    private FlightsController flightsController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(flightsController).build();
    }

    @Test
    void testGetFlightsByFilters_WithValidParams() throws Exception {
        Flight flight1 = generateFlight("TEST1","AMS", LocalDateTime.now().minusDays(10), FlightDirection.DEPARTURE, Status.DEPARTED);
        Flight flight2 = generateFlight("TEST2", "JFK", LocalDateTime.now().minusDays(3), FlightDirection.ARRIVAL, Status.SCHEDULED);
        List<Flight> mockFlights = List.of(flight1, flight2);

        when(flightsService.getFlightsByFilters(any(), any(), any(), any(), any())).thenReturn(mockFlights);

        mockMvc.perform(get("/flights")
                        .param("destination", "JFK")
                        .param("flightDirection", "ARRIVAL")
                        .param("status", "SCHEDULED")
                        .param("startScheduleDateTime", "2024-01-01")
                        .param("endScheduleDateTime", "2024-12-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].destination").value("AMS"))
                .andExpect(jsonPath("$[1].destination").value("JFK"))
                .andExpect(jsonPath("$[0].flightDirection").value("DEPARTURE"))
                .andExpect(jsonPath("$[1].flightDirection").value("ARRIVAL"));

        verify(flightsService, times(1)).getFlightsByFilters(any(), any(), any(), any(), any());
    }

    @Test
    void testGetFlightsByFilters_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/flights")
                        .param("destination", "JFK")
                        .param("flightDirection", "ARRIVAL")
                        .param("status", "SCHEDULED")
                        .param("startScheduleDateTime", "2024-01-01")
                        .param("endScheduleDateTime", "invalid-date")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFlightsByFilters_InvalidDestination() throws Exception {
        mockMvc.perform(get("/flights")
                        .param("destination", "XYZABC") // Invalid destination
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFlightsByFilters_InvalidStatus() throws Exception {
        mockMvc.perform(get("/flights")
                        .param("status", "INVALID")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFlightsByFilters_InternalServerError() throws Exception {
        when(flightsService.getFlightsByFilters(any(), any(), any(), any(), any()))
                .thenThrow(new IOException("Something went wrong"));

        mockMvc.perform(get("/flights")
                        .param("destination", "JFK")
                        .param("flightDirection", "ARRIVAL")
                        .param("status", "SCHEDULED")
                        .param("startScheduleDateTime", "2024-01-01")
                        .param("endScheduleDateTime", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong while processing your request."));
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
