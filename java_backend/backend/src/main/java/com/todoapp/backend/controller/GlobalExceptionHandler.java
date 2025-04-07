package com.todoapp.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.err.println(" Wystąpił wyjątek: " + e.getMessage());
        e.printStackTrace(); //  pokazuje całą przyczynę w konsoli
        return new ResponseEntity<>("Wewnętrzny błąd serwera: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
