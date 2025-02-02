package com.mohitraj.loadbalancer.algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoadBalancingAlgorithmTest {

    @Test
    public void testSelectServerWithEmptyList() {
        // Arrange
        List<String> servers = Collections.emptyList();
        LoadBalancingAlgorithm randomAlgorithm = new RandomAlgorithm();
        LoadBalancingAlgorithm roundRobinAlgorithm = new RoundRobinAlgorithm();

        // Act
        String randomSelectedServer = randomAlgorithm.selectServer(servers);
        String roundRobinSelectedServer = roundRobinAlgorithm.selectServer(servers);

        // Assert
        assertNull(randomSelectedServer);
        assertNull(roundRobinSelectedServer);
    }

    @Test
    public void testSelectServerWithMultipleServers() {
        // Arrange
        List<String> servers = Arrays.asList("server1", "server2", "server3", "server4");
        LoadBalancingAlgorithm randomAlgorithm = new RandomAlgorithm();
        LoadBalancingAlgorithm roundRobinAlgorithm = new RoundRobinAlgorithm();

        // Act
        String randomSelectedServer = randomAlgorithm.selectServer(servers);
        String roundRobinSelectedServer = roundRobinAlgorithm.selectServer(servers);

        // Assert
        assertNotNull(randomSelectedServer);
        assertTrue(servers.contains(randomSelectedServer));
        assertNotNull(roundRobinSelectedServer);
        assertTrue(servers.contains(roundRobinSelectedServer));
    }
}


