package com.assignment.flightAPI.config;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class LocalElasticsearchContainer {

    private static final  ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.10.0")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false");

    static {
        container.start();
        System.setProperty("spring.elasticsearch.port", String.valueOf(container.getFirstMappedPort()));
    }

    public static String getElasticsearchUrl() {
        return container.getHttpHostAddress();
    }
}
