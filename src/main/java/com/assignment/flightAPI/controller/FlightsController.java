package com.assignment.flightAPI.controller;

import com.assignment.flightAPI.model.Flight;
import com.assignment.flightAPI.model.FlightDirection;
import com.assignment.flightAPI.model.Status;
import com.assignment.flightAPI.services.FlightsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;

@RestController
public class FlightsController {

    private static final Logger logger = LoggerFactory.getLogger(FlightsController.class);

    @Autowired
    FlightsService flightsService;

    @GetMapping("/flights")
    @Operation(summary = "Get flights by filters", description = "Retrieves a list of flights filtered by optional parameters.")
    public List<Flight> getFlightsByFilters(@RequestParam(required = false) @Pattern(regexp = "^[a-zA-Z]{3}$") String destination,
                                            @RequestParam(required = false) FlightDirection flightDirection,
                                            @RequestParam(required = false) Status status,
                                            @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String startScheduleDateTime,
                                            @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String endScheduleDateTime) throws IOException {
        logger.info("Request came in to search for flights.");
        List<Flight> result =  flightsService.getFlightsByFilters(destination, flightDirection, status, startScheduleDateTime, endScheduleDateTime);
        logger.info("Result size is - {}",result.size());
        return result;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleInvalidEnum(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Invalid value provided for status or flightDirection.";
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<String> handleInvalidPatternMatch(HandlerMethodValidationException ex) {
        String errorMessage = "Pattern of destination or start/end date is not matched.";
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleInternalException(IOException ex) {
        return new ResponseEntity<>("Something went wrong while processing your request.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
