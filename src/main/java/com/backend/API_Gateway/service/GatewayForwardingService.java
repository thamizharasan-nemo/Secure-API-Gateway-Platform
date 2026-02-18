package com.backend.API_Gateway.service;

import com.backend.API_Gateway.discovery.ServiceRegistry;
import com.backend.API_Gateway.loadbalancer.LoadBalancer;
import com.backend.API_Gateway.routing.RouteResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GatewayForwardingService {

    private final WebClient webClient;
    private final ServiceRegistry serviceRegistry;
    private final LoadBalancer loadBalancer;
    private final RouteResolver routeResolver;

    public GatewayForwardingService(WebClient webClient, ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, RouteResolver routeResolver) {
        this.webClient = webClient;
        this.serviceRegistry = serviceRegistry;
        this.loadBalancer = loadBalancer;
        this.routeResolver = routeResolver;
    }

    public ResponseEntity<String> forwardRequest(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        String userId = String.valueOf(auth.getPrincipal());
        String requestId = MDC.get("requestId");

        String serviceName = routeResolver.resolve(request);

        List<String> instances = serviceRegistry.getInstances("user-service");

        // get any instance for that service to forward the request
        String baseUrl = loadBalancer.chooseInstance(instances);

        return webClient.get()
                .uri(baseUrl + "/internal/data")
                .header("X-User-Id", userId)
                .header("X-Request-Id", requestId)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
