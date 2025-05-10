package com.mohitraj.loadbalancer.controller;

import com.mohitraj.loadbalancer.service.LoadBalancerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @PostMapping("/setAlgorithm")
    public String setAlgorithm(@RequestParam String algo) {
        return loadBalancerService.setAlgorithm(algo);
    }

    @GetMapping("/proxy")
    public String forwardRequest(@RequestParam String path) throws Exception {
        return loadBalancerService.forwardRequest(path);
    }

    @GetMapping("/servers")
    public List<String> listServers() {
        return loadBalancerService.getServers();
    }

    @PostMapping("/servers")
    public String addServer(@RequestParam String serverUrl) {
        return loadBalancerService.addServer(serverUrl);
    }

    @DeleteMapping("/servers")
    public String removeServer(@RequestParam String serverUrl) {
        return loadBalancerService.removeServer(serverUrl);
    }
}
