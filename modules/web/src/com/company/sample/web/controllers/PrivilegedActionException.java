package com.company.sample.web.controllers;

public class PrivilegedActionException extends RuntimeException {
    public PrivilegedActionException(Throwable cause) {
        super(cause);
    }
}