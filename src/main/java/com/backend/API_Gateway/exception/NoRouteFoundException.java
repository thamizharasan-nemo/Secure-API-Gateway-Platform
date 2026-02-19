package com.backend.API_Gateway.exception;

public class NoRouteFoundException extends RuntimeException{
    public NoRouteFoundException(String path){
        super("No route found for path "+ path);
    }
}
