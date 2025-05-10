package com.mohitraj.loadbalancer.service;

import com.mohitraj.loadbalancer.config.ConfigProperties;
import com.mohitraj.loadbalancer.algorithm.LoadBalancingAlgorithm;
import com.mohitraj.loadbalancer.algorithm.RoundRobinAlgorithm;
import com.mohitraj.loadbalancer.algorithm.RandomAlgorithm;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RefreshScope
@Service
public class LoadBalancerService {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerService.class);
    private final ConfigProperties configProperties;
    private HttpClient client;
    private final Map<String, LoadBalancingAlgorithm> algorithms = new HashMap<>();
    private LoadBalancingAlgorithm currentAlgorithm;
    private final List<String> allServers = new CopyOnWriteArrayList<>();
    private final List<String> activeServers = new CopyOnWriteArrayList<>();

    @Autowired
    public LoadBalancerService(ConfigProperties configProperties, HttpClient client) {
        this.configProperties = configProperties;
        algorithms.put("round-robin", new RoundRobinAlgorithm());
        algorithms.put("random", new RandomAlgorithm());
        this.client = client != null ? client : HttpClient.newHttpClient();

        loadConfiguration();
    }

    @EventListener(EnvironmentChangeEvent.class)
    public void loadConfiguration() {
        allServers.clear();
        allServers.addAll(configProperties.getServers());

        activeServers.clear();
        activeServers.addAll(allServers);

        setAlgorithm(configProperties.getAlgorithm());
        logger.info("Load balancer configuration loaded with algorithm: {} and servers: {}", currentAlgorithm, allServers);
    }

    public String setAlgorithm(String algo) {
        if (algorithms.containsKey(algo)) {
            currentAlgorithm = algorithms.get(algo);
            return "Load balancing algorithm set to " + algo;
        }
        return "Invalid algorithm. Choose 'round-robin' or 'random'";
    }

    public List<String> getServers() {
        return new ArrayList<>(allServers);
    }

    public List<String> getActiveServers() {
        return new ArrayList<>(activeServers);
    }

    public String addServer(String serverUrl) {
        if (!allServers.contains(serverUrl)) {
            allServers.add(serverUrl);
            logger.info("Server {} added to the list of all servers", serverUrl);
            return "Server added successfully. It will be added to active servers if it passes health check.";
        }
        return "Server already exists.";
    }

    public String removeServer(String serverUrl) {
        if (allServers.remove(serverUrl)) {
            activeServers.remove(serverUrl);
            logger.info("Server {} removed from all servers", serverUrl);
            return "Server removed successfully.";
        }
        return "Server not found.";
    }

    @Scheduled(fixedRate = 5000)
    public void healthCheck() {
        for (String server : allServers) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(server + "/health"))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    if (!activeServers.contains(server)) {
                        activeServers.add(server);
                        logger.info("Server {} is back online", server);
                    }
                } else {
                    activeServers.remove(server);
                    logger.warn("Server {} is unhealthy", server);
                }
            } catch (Exception e) {
                activeServers.remove(server);
                logger.error("Error checking server {}: {}", server, e.getMessage());
            }
        }
    }

    public String forwardRequest(String path) {
        try {
            if (activeServers.isEmpty()) {
                return "No available servers";
            }
            String server = currentAlgorithm.selectServer(activeServers);
            if (server == null) return "No available servers";
            logger.info("Server {} selected using {}", server, currentAlgorithm);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server + path))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            logger.error("Request failed: {}", e.getMessage());
            return "Error forwarding request";
        }
    }
}
