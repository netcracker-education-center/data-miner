package org.netcracker.learningcenter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be thrown if it was not possible
 * to find a reader class for a file with its extension
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReaderNotFoundException extends Exception {
    public ReaderNotFoundException(String message) {
        super(message);
    }
}
