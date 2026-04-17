package com.accenture.franchise.dto;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}