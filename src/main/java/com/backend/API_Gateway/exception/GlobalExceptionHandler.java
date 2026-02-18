package com.backend.API_Gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleExceptions(Exception ex){
//        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> handleRunTimeExceptions(RuntimeException rx){
//        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, rx.getMessage());
//    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ux){
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, ux.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException fx){
        return buildExceptionResponse(HttpStatus.FORBIDDEN, fx.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<?> handleTooManyRequests(TooManyRequestsException ex) {
        return buildExceptionResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException brx){
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, brx.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return buildExceptionResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    public ResponseEntity<?> buildExceptionResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "error", status.getReasonPhrase(),
                        "status", status.value(),
                        "message", message
                )
        );
    }
}
