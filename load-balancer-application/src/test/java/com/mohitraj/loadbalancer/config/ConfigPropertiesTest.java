package com.mohitraj.loadbalancer.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(HttpClientConfig.class) // Ensure this is imported if not automatically scanned
class ConfigPropertiesTest {

    private ConfigProperties configProperties;

    @BeforeEach
    void setUp() {
        configProperties = new ConfigProperties();
    }

    @Test
    void testGetAndSetServers() {
        // Arrange
        List<String> servers = Arrays.asList("http://localhost:8081", "http://localhost:8082");

        // Act
        configProperties.setServers(servers);
        List<String> result = configProperties.getServers();

        // Assert
        assertEquals(servers, result);
    }

    @Test
    void testGetAndSetAlgorithm() {
        // Arrange
        String algorithm = "round-robin";

        // Act
        configProperties.setAlgorithm(algorithm);
        String result = configProperties.getAlgorithm();

        // Assert
        assertEquals(algorithm, result);
    }

    @Test
    void testEmptyServers() {
        // Arrange
        List<String> servers = List.of(); // Empty list

        // Act
        configProperties.setServers(servers);
        List<String> result = configProperties.getServers();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullAlgorithm() {
        // Arrange
        String algorithm = null;

        // Act
        configProperties.setAlgorithm(algorithm);
        String result = configProperties.getAlgorithm();

        // Assert
        assertNull(result);
    }
}