package com.backend.API_Gateway.routing;

import jakarta.servlet.http.HttpServletRequest;

public interface RouteResolver {
    String resolve(HttpServletRequest httpServletRequest);
}
