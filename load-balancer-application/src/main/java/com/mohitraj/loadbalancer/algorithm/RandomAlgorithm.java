package com.mohitraj.loadbalancer.algorithm;

import java.util.List;
import java.util.Random;

public class RandomAlgorithm implements LoadBalancingAlgorithm {
    private final Random random = new Random();

    @Override
    public String selectServer(List<String> servers) {
        if (servers.isEmpty()) return null;
        return servers.get(random.nextInt(servers.size()));
    }
}
