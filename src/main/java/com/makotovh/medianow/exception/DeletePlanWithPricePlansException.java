package com.makotovh.medianow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeletePlanWithPricePlansException extends RuntimeException {
    public DeletePlanWithPricePlansException(String planCode) {
        super("Cannot delete plan "+ planCode +" because it has price plans");
    }
}
