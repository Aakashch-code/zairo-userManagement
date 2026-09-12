package com.example.zairo.authentication.domain.exception;

public class InvalidTaskAssignmentException extends RuntimeException {

    public InvalidTaskAssignmentException(String message) {
        super(message);
    }
}