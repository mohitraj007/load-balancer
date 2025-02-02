package com.mohitraj.loadbalancer.algorithm;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinAlgorithm implements LoadBalancingAlgorithm {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String selectServer(List<String> servers) {
        if (servers.isEmpty()) return null;
        return servers.get(index.getAndIncrement() % servers.size());
    }
}