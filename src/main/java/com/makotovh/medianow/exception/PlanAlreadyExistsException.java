package com.makotovh.medianow.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(value = CONFLICT)
public class PlanAlreadyExistsException extends RuntimeException {
    public PlanAlreadyExistsException(String planCope) {
        super("Plan with code '" + planCope + "' already exists");
    }
}