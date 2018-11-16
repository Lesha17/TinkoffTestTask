package com.lmm.tinkoff.task.search;

/**
 * Indicates that some exception occurred while searching for a number.
 */
public class FindNumberException extends RuntimeException {
    public FindNumberException(Throwable cause) {
        super(cause);
    }
}
