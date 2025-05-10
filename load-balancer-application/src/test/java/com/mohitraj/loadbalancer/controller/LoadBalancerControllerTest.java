package com.mohitraj.loadbalancer.controller;

import com.mohitraj.loadbalancer.service.LoadBalancerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LoadBalancerControllerTest {

    @Mock
    private LoadBalancerService loadBalancerService;

    @InjectMocks
    private LoadBalancerController loadBalancerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetAlgorithm() {
        // Arrange
        String algorithm = "round-robin";
        when(loadBalancerService.setAlgorithm(algorithm)).thenReturn("Algorithm set to: " + algorithm);

        // Act
        String response = loadBalancerController.setAlgorithm(algorithm);

        // Assert
        assertEquals("Algorithm set to: " + algorithm, response);
        verify(loadBalancerService, times(1)).setAlgorithm(algorithm);
    }

    @Test
    void testForwardRequest() throws Exception {
        // Arrange
        String path = "/data";
        String expectedResponse = "Response from backend server";
        when(loadBalancerService.forwardRequest(path)).thenReturn(expectedResponse);

        // Act
        String response = loadBalancerController.forwardRequest(path);

        // Assert
        assertEquals(expectedResponse, response);
        verify(loadBalancerService, times(1)).forwardRequest(path);
    }

    @Test
    void testListServers() {
        // Arrange
        List<String> servers = Arrays.asList("http://localhost:8081", "http://localhost:8082");
        when(loadBalancerService.getServers()).thenReturn(servers);

        // Act
        List<String> response = loadBalancerController.listServers();

        // Assert
        assertEquals(servers, response);
        verify(loadBalancerService, times(1)).getServers();
    }

    @Test
    void testAddServer() {
        // Arrange
        String serverUrl = "http://localhost:8083";
        when(loadBalancerService.addServer(serverUrl)).thenReturn("Server added: " + serverUrl);

        // Act
        String response = loadBalancerController.addServer(serverUrl);

        // Assert
        assertEquals("Server added: " + serverUrl, response);
        verify(loadBalancerService, times(1)).addServer(serverUrl);
    }

    @Test
    void testRemoveServer() {
        // Arrange
        String serverUrl = "http://localhost:8083";
        when(loadBalancerService.removeServer(serverUrl)).thenReturn("Server removed: " + serverUrl);

        // Act
        String response = loadBalancerController.removeServer(serverUrl);

        // Assert
        assertEquals("Server removed: " + serverUrl, response);
        verify(loadBalancerService, times(1)).removeServer(serverUrl);
    }
}