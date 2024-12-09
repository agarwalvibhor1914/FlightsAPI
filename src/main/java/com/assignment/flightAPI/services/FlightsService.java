package com.assignment.flightAPI.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.assignment.flightAPI.model.Flight;
import com.assignment.flightAPI.model.FlightDirection;
import com.assignment.flightAPI.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightsService {

    private static final Logger logger = LoggerFactory.getLogger(FlightsService.class);

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    //in documentation- another model in return type can be used
    public List<Flight> getFlightsByFilters(String destination, FlightDirection flightDirection,
                                            Status status, String startScheduleDateTime, String endScheduleDateTime) throws IOException {
        logger.debug("Generating queries for search.");
        List<Query> queries = generateQueries(destination, flightDirection, status, startScheduleDateTime, endScheduleDateTime);
        logger.info("Queries generated for search, queries size is {}", queries.size());
        Query combinedQuery = QueryBuilders.bool()
                .must(queries)
                .build()._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index("flight") // Index name
                .query(combinedQuery)
                .size(5000)
        );
        logger.debug("Going to execute query for searching.");
        SearchResponse<Flight> response = elasticsearchClient.search(request, Flight.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
    }

    private List<Query> generateQueries(String destination, FlightDirection flightDirection,
                                        Status status, String startScheduleDateTime, String endScheduleDateTime){
        List<Query> queries = new ArrayList<>();

        Optional.ofNullable(destination).ifPresent(value->queries.add(getQueryForTextFields("destination.keyword", value)));
        Optional.ofNullable(flightDirection).ifPresent(value->queries.add(getQueryForTextFields("flightDirection.keyword", value.name())));
        Optional.ofNullable(status).ifPresent(value->queries.add(getQueryForTextFields("status.keyword", value.name())));

        if (startScheduleDateTime != null && endScheduleDateTime != null) {
            queries.add(getQueryForDateRangeField(startScheduleDateTime, endScheduleDateTime));
        }
        return queries;
    }

    private Query getQueryForTextFields(String fieldKey, String fieldValue){
        return QueryBuilders.term().field(fieldKey).value(fieldValue).build()._toQuery();
    }

    private Query getQueryForDateRangeField(String startDate, String endDate){
       return  QueryBuilders.range(r -> r
                .field("scheduleDateTime")
                .gte(JsonData.fromJson("\"" + getFormattedDate(startDate, "T00:00:00.000") + "\""))
                .lte(JsonData.fromJson("\"" + getFormattedDate(endDate, "T23:59:59.999") + "\"")));
    }

    private String getFormattedDate(String inputDate, String addition){
        return inputDate.contains("T") ? inputDate : inputDate + addition;
    }
}
