package com.mohitraj.loadbalancer.algorithm;

import java.util.List;

public interface LoadBalancingAlgorithm {
    String selectServer(List<String> servers);
}