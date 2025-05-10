package com.mohitraj.loadbalancer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
@RefreshScope
@Component
@ConfigurationProperties(prefix = "loadbalancer")
public class ConfigProperties {
    private List<String> servers;
    private String algorithm;

    public List<String> getServers() { return servers; }
    public void setServers(List<String> servers) { this.servers = servers; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}