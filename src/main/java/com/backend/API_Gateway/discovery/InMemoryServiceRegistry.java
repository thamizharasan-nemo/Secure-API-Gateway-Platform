package com.backend.API_Gateway.discovery;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryServiceRegistry implements ServiceRegistry{

    private final Map<String, List<String>> registry = new HashMap<>();

    @PostConstruct
    void init(){

        //Hard Coded Service and it's instances(running ports)
        registry.put("user-service", List.of(
                "http://localhost:8081",
                "http://localhost:8082")
        );
        registry.put("feedback-service", List.of(
                "http://localhost:8081",
                "http://localhost:8082")
        );
        registry.put("payment-service", List.of(
                "http://localhost:8081",
                "http://localhost:8082")
        );
    }

    @Override
    public List<String> getInstances(String serviceName) {
        return registry.getOrDefault(serviceName, List.of());
    }
}
