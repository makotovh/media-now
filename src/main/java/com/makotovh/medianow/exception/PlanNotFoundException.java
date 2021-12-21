package com.makotovh.medianow.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class PlanNotFoundException extends RuntimeException {
    public PlanNotFoundException(String id) {
      super("Could not find Plan with id " + id);
    }
}
