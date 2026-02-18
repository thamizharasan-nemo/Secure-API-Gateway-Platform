package com.backend.API_Gateway.routing;

public class RouteDefinition {

    private final String pathPattern;
    private final String serviceName;

    public RouteDefinition(String pathPattern, String serviceName) {
        this.pathPattern = pathPattern;
        this.serviceName = serviceName;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public String getServiceName() {
        return serviceName;
    }
}
