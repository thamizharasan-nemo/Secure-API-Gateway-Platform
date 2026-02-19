package com.backend.API_Gateway.routing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gateway")
@Component
public class RouteProperties {

    private List<RouteDefinition> routes = new ArrayList<>();

    public List<RouteDefinition> getRoutes(){
        return routes;
    }

    public void setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
    }
}
