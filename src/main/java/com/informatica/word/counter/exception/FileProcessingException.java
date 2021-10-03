/*
 * Copyright information here.
 */
package com.informatica.word.counter.exception;

/**
 * An exception for file processing related issues.
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(final String message) {
        super(message);
    }

    public FileProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
