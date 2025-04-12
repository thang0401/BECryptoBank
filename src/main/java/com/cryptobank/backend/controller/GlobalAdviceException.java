package com.cryptobank.backend.controller;

import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAdviceException {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        Map<String, String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResourceNotFoundException> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<AlreadyExistException> handleResourceNotFound(AlreadyExistException ex) {
        return new ResponseEntity<>(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<UsernameNotFoundException> handleUsernameNotFound(UsernameNotFoundException ex) {
        return new ResponseEntity<>(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAnyException(Exception ex) {
        String errorMessage = "An error occurred: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
   
    }

}
