package com.bv.processingapp.api.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class ExceptionHanlder {

    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String INVALID_PARAMETER_ERROR_MESSAGE = "Invalid request parameter provided. Error message: {}";

    @ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST, reason = BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(final ConstraintViolationException ex) {
        log.error(INVALID_PARAMETER_ERROR_MESSAGE, ex.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = INVALID_PARAMETER_ERROR_MESSAGE)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        log.error(INVALID_PARAMETER_ERROR_MESSAGE, ex.getLocalizedMessage());
    }

}
