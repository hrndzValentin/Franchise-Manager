package com.accenture.franchise.controller;
import com.accenture.franchise.dto.BadRequestException;
import com.accenture.franchise.dto.ErrorResponse;
import com.accenture.franchise.dto.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 404
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(
            NotFoundException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                List.of()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    // 🟠 400 - errores de negocio
    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(
            BadRequestException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                List.of()
        );

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    // 🟡 400 - validaciones @Valid
    @ExceptionHandler(org.springframework.web.bind.support.WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            org.springframework.web.bind.support.WebExchangeBindException ex,
            ServerWebExchange exchange) {

        List<String> details = ex.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        ErrorResponse error = buildError(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                exchange.getRequest().getPath().value(),
                details
        );

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    // 🔥 fallback (muy importante)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(
            Exception ex,
            ServerWebExchange exchange) {

        ErrorResponse error = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                exchange.getRequest().getPath().value(),
                List.of(ex.getMessage())
        );

        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
        );
    }

    // 🧩 helper
    private ErrorResponse buildError(
            HttpStatus status,
            String message,
            String path,
            List<String> details) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .details(details)
                .build();
    }
}