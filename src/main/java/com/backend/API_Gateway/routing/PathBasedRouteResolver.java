package com.backend.API_Gateway.routing;

import com.backend.API_Gateway.exception.NoRouteFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class PathBasedRouteResolver implements RouteResolver{

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final RouteProperties routeProperties;


    public PathBasedRouteResolver(RouteProperties routeProperties) {
        this.routeProperties = routeProperties;
    }

    @Override
    public String resolve(HttpServletRequest request) {

        String path = request.getRequestURI();

        return routeProperties.getRoutes()
                .stream()
                .filter(route ->
                        antPathMatcher.match(route.getPathPattern(), path)
                )
                .map(routeDefinition -> routeDefinition.getServiceName())
                .findFirst()
                .orElseThrow(() ->
                        new NoRouteFoundException(path)
                );
    }
}
