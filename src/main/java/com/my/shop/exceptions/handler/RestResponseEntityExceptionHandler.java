package com.my.shop.exceptions.handler;

import com.my.shop.exceptions.*;
import com.my.shop.payloads.responses.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ExceptionHandler(NotEnoughProductException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughProductException(NotEnoughProductException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                e.getMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                e.getMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ProductAlreadyExistsException.class,
                                UserAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(RuntimeException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                e.getMessage()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMisMatchException(PasswordMismatchException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                e.getMessage()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                Objects.requireNonNull(e.getFieldError()).getDefaultMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.debug(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                System.currentTimeMillis(),
                "Incorrect username or password"
        ), HttpStatus.UNAUTHORIZED);
    }
}
