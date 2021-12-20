package com.makotovh.medianow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PricePlanNotFoundException extends RuntimeException {
    public PricePlanNotFoundException(long id) {
        super("Price plan with id " + id + " not found");
    }
}
