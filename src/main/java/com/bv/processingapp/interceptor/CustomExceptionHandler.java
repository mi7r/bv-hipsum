package com.bv.processingapp.interceptor;

import com.bv.processingapp.exception.ParagraphProcessingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    private static final String INVALID_PARAMETER_ERROR_MESSAGE = "Invalid request parameter provided. Error message: {}";

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(final ConstraintViolationException ex) {
        log.error(INVALID_PARAMETER_ERROR_MESSAGE, ex.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        log.error(INVALID_PARAMETER_ERROR_MESSAGE, ex.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = INTERNAL_ERROR)
    @ExceptionHandler(ParagraphProcessingException.class)
    public void  handleParagraphProcessingException(final ParagraphProcessingException ex) {
        log.error("Error while processing paragraph: {}", ex.getLocalizedMessage());
    }

}
