package com.makotovh.medianow.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(long id) {
      super("Could not find package with id " + id);
    }
}
