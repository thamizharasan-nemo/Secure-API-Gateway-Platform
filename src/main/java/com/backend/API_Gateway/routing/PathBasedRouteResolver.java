package com.backend.API_Gateway.routing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class PathBasedRouteResolver implements RouteResolver{

    private final List<RouteDefinition> routes;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();


    public PathBasedRouteResolver(List<RouteDefinition> routes) {
        this.routes = List.of(
                new RouteDefinition("/feedback/**", "feedback-service"),
                new RouteDefinition("/chat/**", "chat-service"),
                new RouteDefinition("/queue/**", "queue-service")
        );
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();

        return routes.stream()
                .filter(route -> antPathMatcher.match(route.getPathPattern(), path))
                .map(routeDefinition -> routeDefinition.getServiceName())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No route found for path: " + path)
                );
    }
}
