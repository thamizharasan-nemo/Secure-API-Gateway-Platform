package com.backend.API_Gateway.loadbalancer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoundRobinLoadBalancer implements LoadBalancer{

    private final AtomicInteger index = new AtomicInteger();

    @Override
    public String chooseInstance(List<String> instances) {
        if (instances.isEmpty()){
            throw new IllegalStateException("No service instances available");
        }
        int atPosition = Math.abs(index.getAndIncrement() % instances.size());
        return instances.get(atPosition);
    }
}
