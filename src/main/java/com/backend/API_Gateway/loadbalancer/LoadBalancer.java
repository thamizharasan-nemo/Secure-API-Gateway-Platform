package com.backend.API_Gateway.loadbalancer;

import java.util.List;

public interface LoadBalancer {
    String chooseInstance(List<String> instances);
}
