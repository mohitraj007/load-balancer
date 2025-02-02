package com.mohitraj.loadbalancer.algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

        import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomAlgorithmTest {

    @Test
    public void testSelectServer() {
        // Arrange
        List<String> servers = Arrays.asList("server1", "server2", "server3");
        RandomAlgorithm algorithm = new RandomAlgorithm();

        // Act
        String selectedServer = algorithm.selectServer(servers);

        // Assert
        assertNotNull(selectedServer);
        assertTrue(servers.contains(selectedServer));
    }
}

