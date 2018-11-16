package com.lmm.tinkoff.task.endpoint;

/**
 * Indicates that client gives too large number for input.
 */
public class NumberIsTooLargeException extends IllegalArgumentException {

    public NumberIsTooLargeException() {
        super("Given number is too large");
    }
}
