package com.mohitraj.loadbalancer.service;

import com.mohitraj.loadbalancer.algorithm.LoadBalancingAlgorithm;
import com.mohitraj.loadbalancer.algorithm.RandomAlgorithm;
import com.mohitraj.loadbalancer.algorithm.RoundRobinAlgorithm;
import com.mohitraj.loadbalancer.config.ConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RefreshScope
class LoadBalancerServiceTest {

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock
    private LoadBalancingAlgorithm roundRobinAlgorithm;

    @Mock
    private LoadBalancingAlgorithm randomAlgorithm;

    @InjectMocks
    private LoadBalancerService loadBalancerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Disabled("Skipping this test for now")
    @Test
    void testSetAlgorithm() {
        // Arrange
        loadBalancerService = new LoadBalancerService(configProperties);
        loadBalancerService.setAlgorithm("round-robin"); // Default algorithm

        when(configProperties.getAlgorithm()).thenReturn("round-robin");

        // Act
        String result = loadBalancerService.setAlgorithm("random");

        // Assert
        assertEquals("Load balancing algorithm set to random", result);
        verify(configProperties, times(2)).getAlgorithm();
    }

    @Test
    void testSetAlgorithmInvalid() {
        // Arrange
        loadBalancerService = new LoadBalancerService(configProperties);
        loadBalancerService.setAlgorithm("round-robin"); // Default algorithm

        when(configProperties.getAlgorithm()).thenReturn("round-robin");

        // Act
        String result = loadBalancerService.setAlgorithm("invalid-algo");

        // Assert
        assertEquals("Invalid algorithm. Choose 'round-robin' or 'random'", result);
    }

    @Test
    void testGetServers() {
        // Arrange
        List<String> servers = Arrays.asList("http://localhost:8081", "http://localhost:8082");
        when(configProperties.getServers()).thenReturn(servers);
        loadBalancerService = new LoadBalancerService(configProperties);
        loadBalancerService.setAlgorithm("round-robin"); // Default algorithm

        // Act
        List<String> result = loadBalancerService.getServers();

        // Assert
        assertEquals(servers, result);
        verify(configProperties, times(2)).getServers();
    }

    @Test
    void testAddServer() {
        // Arrange
        loadBalancerService = new LoadBalancerService(configProperties);
        loadBalancerService.setAlgorithm("round-robin"); // Default algorithm

        String serverUrl = "http://localhost:8083";

        // Act
        String result = loadBalancerService.addServer(serverUrl);

        // Assert
        assertEquals("Server added successfully.", result);
        assertTrue(loadBalancerService.getServers().contains(serverUrl));
    }

    @Disabled("Skipping this test for now")
    @Test
    void testAddServerAlreadyExists() {
        // Arrange
        String serverUrl = "http://localhost:8081";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));

        // Act
        String result = loadBalancerService.addServer(serverUrl);

        // Assert
        assertEquals("Server already exists in the active list.", result);
    }

    @Disabled("Skipping this test for now")
    @Test
    void testRemoveServer() {
        // Arrange
        String serverUrl = "http://localhost:8081";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));

        // Act
        String result = loadBalancerService.removeServer(serverUrl);

        // Assert
        assertEquals("Server removed successfully.", result);
        assertFalse(loadBalancerService.getServers().contains(serverUrl));
    }

    @Test
    void testRemoveServerNotFound() {
        // Arrange
        String serverUrl = "http://localhost:8081";

        // Act
        String result = loadBalancerService.removeServer(serverUrl);

        // Assert
        assertEquals("Server not found in the active list.", result);
    }

    @Disabled("Skipping this test for now")
    @Test
    void testForwardRequest() throws Exception {
        // Arrange
        String serverUrl = "http://localhost:8081";
        String path = "/data";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));
        when(roundRobinAlgorithm.selectServer(anyList())).thenReturn(serverUrl);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn("Response from server");
        loadBalancerService = new LoadBalancerService(configProperties);
        loadBalancerService.setAlgorithm("round-robin"); // Default algorithm

        // Act
        String result = loadBalancerService.forwardRequest(path);

        // Assert
        assertEquals("Response from server", result);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testForwardRequestNoServers() {
        // Arrange
        when(configProperties.getServers()).thenReturn(Arrays.asList());

        // Act
        String result = loadBalancerService.forwardRequest("/data");

        // Assert
        assertEquals("No available servers", result);
    }

    @Disabled("Skipping this test for now")
    @Test
    void testHealthCheck() throws Exception {
        // Arrange
        String serverUrl = "http://localhost:8081";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);

        // Act
        loadBalancerService.healthCheck();

        // Assert
        assertTrue(loadBalancerService.getServers().contains(serverUrl));
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Disabled("Skipping this test for now")
    @Test
    void testHealthCheckUnhealthyServer() throws Exception {
        // Arrange
        String serverUrl = "http://localhost:8081";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        // Act
        loadBalancerService.healthCheck();

        // Assert
        assertFalse(loadBalancerService.getServers().contains(serverUrl));
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Disabled("Skipping this test for now")
    @Test
    void testHealthCheckException() throws Exception {
        // Arrange
        String serverUrl = "http://localhost:8081";
        when(configProperties.getServers()).thenReturn(Arrays.asList(serverUrl));
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new RuntimeException("Connection error"));

        // Act
        loadBalancerService.healthCheck();

        // Assert
        assertFalse(loadBalancerService.getServers().contains(serverUrl));
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}