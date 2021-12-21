package com.makotovh.medianow.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(value = CONFLICT, reason = "Price plan already exists")
public class PricePlanAlreadyExistsException extends RuntimeException {
    public PricePlanAlreadyExistsException(String planCode, String countryCode) {
        super(String.format("Price plan with code %s already exists for country %s", planCode, countryCode));
    }
}
