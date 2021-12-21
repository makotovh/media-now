package com.makotovh.medianow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PricePlanNotFoundException extends RuntimeException {
    public PricePlanNotFoundException(long id) {
        super("Could not find Price Plan with id " + id);
    }

    public PricePlanNotFoundException(String planCode, String countryCode) {
        super("Could not find Price Plan with planCode " + planCode + " and countryCode " + countryCode);
    }
}
