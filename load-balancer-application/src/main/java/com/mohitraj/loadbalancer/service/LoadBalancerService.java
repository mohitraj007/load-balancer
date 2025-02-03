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
    private final List<String> activeServers = new CopyOnWriteArrayList<>();
    private final List<String> manuallyRemovedServers = new CopyOnWriteArrayList<>();

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
        setAlgorithm(configProperties.getAlgorithm());
        activeServers.clear();
        activeServers.addAll(configProperties.getServers());
        logger.info("Load balancer configuration loaded with algorithm: {} and servers: {}", currentAlgorithm, activeServers);
    }

    public String setAlgorithm(String algo) {
        if (algorithms.containsKey(algo)) {
            currentAlgorithm = algorithms.get(algo);
            return "Load balancing algorithm set to " + algo;
        }
        return "Invalid algorithm. Choose 'round-robin' or 'random'";
    }

    public List<String> getServers() {
        return activeServers;
    }

    public String addServer(String serverUrl) {
        if (manuallyRemovedServers.contains(serverUrl)) {
            manuallyRemovedServers.remove(serverUrl);
        }
        if (!activeServers.contains(serverUrl)) {
            activeServers.add(serverUrl);
            logger.info("Server {} added to the active list", serverUrl);
            return "Server added successfully.";
        }
        return "Server already exists in the active list.";
    }

    public String removeServer(String serverUrl) {
        if (activeServers.remove(serverUrl)) {
            manuallyRemovedServers.add(serverUrl);
            logger.info("Server {} removed from the active list", serverUrl);
            return "Server removed successfully.";
        }
        return "Server not found in the active list.";
    }

    @Scheduled(fixedRate = 5000)
    public void healthCheck() {
        for (String server : configProperties.getServers()) {
            if (manuallyRemovedServers.contains(server)) {
                continue; // Skip manually removed servers
            }
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
            List<String> servers = getServers();
            if (servers.isEmpty()) {
                return "No available servers";
            }
            String server = currentAlgorithm.selectServer(servers);
            if (server == null) return "No available servers";

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
